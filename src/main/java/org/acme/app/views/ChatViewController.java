package org.acme.app.views;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.app.dto.ModelProviderEnum;
import org.acme.app.repository.PromptRepository;
import org.acme.assistants.DocumentAssistant;
import org.acme.utils.AiModelFactory;
import org.acme.utils.ContentRetrieverFactory;
import org.acme.utils.DocumentLoader;
import org.acme.utils.EmbeddingFactory;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static org.acme.app.dto.ModelProviderEnum.OPEN_AI;

@Path("chat-view")
public class ChatViewController {

    private static final Logger logger = LoggerFactory.getLogger(ChatViewController.class);

    @Inject
    Template chatView;

    @Inject
    PromptRepository promptRepository;

    private final ModelProviderEnum selectedProvider = OPEN_AI;
    private final ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
    private ContentRetriever contentRetriever;
    private String selectedFilename;
    private String selectedPrompt;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getView() {
        return chatView.data(
                "selectedPrompt", selectedPrompt,
                "selectedFilename", selectedFilename,
                "selectedProvider", selectedProvider,
                "chatMessages", chatMemory.messages(),
                "prompts", promptRepository.findAll());
    }

    @POST()
    @Path("send")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance sendMessage(
        @FormParam("text") String messageText, 
        @FormParam("model") ModelProviderEnum model,
        @FormParam("systemMessage") String systemMessage) {
        
        assert null != messageText;
        assert !messageText.isEmpty();
        assert null != model;
        
        var chatModel = switch(model) {
            case OPEN_AI -> AiModelFactory.createOpenAIChatModel();
            case LOCAL -> AiModelFactory.createLocalChatModel();
        };

        if (Objects.nonNull(systemMessage) && !systemMessage.isEmpty()) {
            chatMemory.add(SystemMessage.from(systemMessage));
        }
        
        chatMemory.add(UserMessage.from(messageText));
        
        if (Objects.nonNull(contentRetriever)) {
            var documentAssistant = new DocumentAssistant(chatModel, contentRetriever);
            var response = documentAssistant.chat(chatMemory.messages());
            chatMemory.add(AiMessage.from(response));
        } else {
            var response = chatModel.generate(chatMemory.messages());
            chatMemory.add(response.content());
        }

        return getView();
    }

    @GET()
    @Path("clear")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance clear() {
        chatMemory.clear();
        contentRetriever = null;
        selectedFilename = null;
        selectedPrompt = null;
        return getView();
    }

    @GET()
    @Path("usePrompt")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance usePrompt(@QueryParam("promptName") String promptName) {
        var prompt = promptRepository.findPrompt(promptName);
        selectedPrompt = prompt.text();
        return getView();
    }


    @POST()
    @Path("upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance uploadFile(@RestForm("file") FileUpload fileUpload) {
        try {
            var content = DocumentLoader.getContent(fileUpload.uploadedFile().toAbsolutePath(), fileUpload.fileName());
            var embeddingModel = EmbeddingFactory.createEmbeddingModel();
            var embeddingStore = EmbeddingFactory.createEmbeddingStore();
            selectedFilename = fileUpload.fileName();
            contentRetriever = ContentRetrieverFactory.createContentRetriever(
                    embeddingModel,
                    embeddingStore,
                    content);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return getView();
    }

}
