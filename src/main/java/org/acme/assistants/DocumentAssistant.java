package org.acme.assistants;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;

import java.util.List;

public class DocumentAssistant {
    private final DocumentAiService documentAiService;

    interface DocumentAiService {
        String chat(String query);
        String chat(List<ChatMessage> query);
    }

    public DocumentAssistant(ChatLanguageModel chatModel, ContentRetriever contentRetriever) {
        this.documentAiService = buildDocumentAiService(chatModel, contentRetriever);
    }

    public String chat(String message) {
        return documentAiService.chat(message);
    }

    public String chat(List<ChatMessage> messages) {
        return documentAiService.chat(messages);
    }

    private DocumentAiService buildDocumentAiService(ChatLanguageModel chatModel, ContentRetriever contentRetriever) {
        return AiServices.builder(DocumentAiService.class)
                .chatLanguageModel(chatModel)
                .contentRetriever(contentRetriever)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

}
