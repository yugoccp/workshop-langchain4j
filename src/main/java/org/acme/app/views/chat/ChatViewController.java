package org.acme.app.views.chat;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.app.repository.PromptRepository;
import org.acme.assistants.ChatAssistant;
import org.acme.utils.AiModelFactory;

import java.net.URI;

@Path("chat-view")
public class ChatViewController {

    @Inject
    Template chatView;

    @Inject
    PromptRepository promptRepository;

    private ChatAssistant chatAssistant;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getView() {
        var chatMessages = chatAssistant.getMessages();
        return chatView.data("chatMessages", chatMessages);
    }

    @GET
    @Path("newChat")
    public Response newChat(@QueryParam("prompt") String promptName, @QueryParam("model") ChatModelTypeEnum model) {
        var prompt = promptRepository.findPrompt(promptName);
        var chatModel = switch(model) {
            case OPEN_AI -> AiModelFactory.createOpenAIChatModel();
            case LOCAL -> AiModelFactory.createLocalChatModel();
        };
        chatAssistant = new ChatAssistant(chatModel, prompt.text());
        return Response.temporaryRedirect(URI.create("/chat-view")).build();
    }

    @POST()
    @Path("send")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance sendMessage(@FormParam("text") String messageText) {
        chatAssistant.chat(messageText);
        var chatMessages = chatAssistant.getMessages();
        return chatView.data("chatMessages", chatMessages);
    }
}
