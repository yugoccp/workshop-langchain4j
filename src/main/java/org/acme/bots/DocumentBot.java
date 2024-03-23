package org.acme.bots;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;


public class DocumentBot {
    private ChatLanguageModel chatModel;
    private ContentRetriever contentRetriever;
    private DocumentAssistant documentAssistant;

    interface DocumentAssistant {
        String answer(String query);
    }

    public DocumentBot(ChatLanguageModel chatModel, ContentRetriever contentRetriever) {
        this.chatModel = chatModel;
        this.contentRetriever = contentRetriever;

        // Build a RAG assistant to select relevant context from the document and add in the user prompt.
        this.documentAssistant = buildDocumentAssistant(chatModel, contentRetriever);

    }

    public String chat(String message) {
        return documentAssistant.answer(message);
    }

    private DocumentAssistant buildDocumentAssistant(ChatLanguageModel chatModel, ContentRetriever contentRetriever) {
        return AiServices.builder(DocumentAssistant.class)
                .chatLanguageModel(chatModel)
                .contentRetriever(contentRetriever)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

}
