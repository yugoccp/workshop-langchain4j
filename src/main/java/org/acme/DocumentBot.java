package org.acme;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class DocumentBot {
    private DocumentAssistant documentAssistant;

    interface DocumentAssistant {
        String answer(String query);
    }

    public DocumentBot(ChatLanguageModel chatModel, String filename) {

        // Transform single file content into chunks of text segments.
        var segments = buildTextSegments(filename);

        // NOTE: same embedding model should be used in embedding store
        // and content retriever to keep search result consistent.
        var embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        // Transform text segments into vector using embedding model
        // and store at embedding storage
        var embeddingStore = buildEmbeddingStore(embeddingModel, segments);

        // Retriever transforms any input into vector using embedding model
        // and return relevant chunks of text from the embedding storage
        // using transformed input.
        var contentRetriever = buildContentRetriever(embeddingModel, embeddingStore);

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

    private EmbeddingStore buildEmbeddingStore(EmbeddingModel embeddingModel, List<TextSegment> segments) {
        var embeddings = embeddingModel.embedAll(segments).content();
        var embeddingStore = new InMemoryEmbeddingStore<TextSegment>();
        embeddingStore.addAll(embeddings, segments);
        return embeddingStore;
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
