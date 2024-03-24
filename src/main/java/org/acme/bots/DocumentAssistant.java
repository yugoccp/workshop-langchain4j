package org.acme.bots;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;

public class DocumentAssistant {
    private DocumentAiService documentAiService;

    interface DocumentAiService {
        String chat(String query);
    }

    public DocumentAssistant(ChatLanguageModel chatModel, ContentRetriever contentRetriever) {
        // Build a RAG assistant to select relevant context from the document and add in the user prompt.
        this.documentAiService = buildDocumentAssistant(chatModel, contentRetriever);

    }

    public String chat(String message) {
        return documentAiService.chat(message);
    }

    private DocumentAiService buildDocumentAssistant(ChatLanguageModel chatModel, ContentRetriever contentRetriever) {
        return AiServices.builder(DocumentAiService.class)
                .chatLanguageModel(chatModel)
                .contentRetriever(contentRetriever)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

}
