package org.acme.bots;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class DocumentBot {
    private ChatLanguageModel chatModel;
    private EmbeddingModel embeddingModel;
    private EmbeddingStore embeddingStore;

    interface DocumentAssistant {
        String answer(String query);
    }

    public DocumentBot(ChatLanguageModel chatModel, EmbeddingModel embeddingModel, EmbeddingStore embeddingStore) {
        this.chatModel = chatModel;
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
    }

    public String chat(String filename, String message) {
        // Transform single file content into chunks of text segments.
        var segments = buildTextSegments(filename);

        // Transform segments into embeddings (vectors)
        var embeddings = embeddingModel.embedAll(segments).content();

        // Store embeddings with the corresponding segments
        embeddingStore.addAll(embeddings, segments);

        var documentAssistant = buildDocumentAssistant(chatModel, embeddingModel, embeddingStore);

        return documentAssistant.answer(message);
    }

    private DocumentAssistant buildDocumentAssistant(ChatLanguageModel chatModel, EmbeddingModel embeddingModel, EmbeddingStore embeddingStore) {
        // Helper class to convert user input into a vector with an embedding model
        // and get related content from the embedding database.
        var contentRetriever = buildContentRetriever(embeddingModel, embeddingStore);

        return AiServices.builder(DocumentAssistant.class)
                .chatLanguageModel(chatModel)
                .contentRetriever(contentRetriever)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    private ContentRetriever buildContentRetriever(EmbeddingModel embeddingModel, EmbeddingStore embeddingStore) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(2)
                .minScore(0.5)
                .build();
    }

    private List<TextSegment> buildTextSegments(String filename) {
        Path documentPath = toPath(filename);
        DocumentParser documentParser = new TextDocumentParser();
        Document document = FileSystemDocumentLoader.loadDocument(documentPath, documentParser);
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);
        return splitter.split(document);
    }

    private Path toPath(String fileName) {
        try {
            URL fileUrl = getClass().getClassLoader().getResource(fileName);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
