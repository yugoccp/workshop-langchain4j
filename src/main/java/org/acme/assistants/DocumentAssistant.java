package org.acme.assistants;

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
        // Build DocumentAiService
        this.documentAiService = buildDocumentAiService(chatModel, contentRetriever);

    }

    public String chat(String message) {
        // Use DocumentAiService to select relevant context from the document
        // and add them to the user prompt.
        return documentAiService.chat(message);
    }

    private DocumentAiService buildDocumentAiService(ChatLanguageModel chatModel, ContentRetriever contentRetriever) {
        return AiServices.builder(DocumentAiService.class)
                .chatLanguageModel(chatModel)
                .contentRetriever(contentRetriever)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

}
