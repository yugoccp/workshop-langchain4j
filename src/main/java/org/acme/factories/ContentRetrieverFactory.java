package org.acme.factories;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ContentRetrieverFactory {

    private ContentRetrieverFactory() {
        throw new IllegalStateException("Factory class shouldn't be instantiated");
    }

    public static ContentRetriever createFileContentRetriever(EmbeddingModel embeddingModel,
            EmbeddingStore<TextSegment> embeddingStore, String filename) {
        // Transform single file content into chunks of text segments.
        var segments = createTextSegments(filename);

        // Transform segments into embeddings (vectors)
        var embeddings = embeddingModel.embedAll(segments).content();

        // Store embeddings with the corresponding segments
        embeddingStore.addAll(embeddings, segments);

        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(5)
                .minScore(0.5)
                .build();
    }

    private static List<TextSegment> createTextSegments(String filename) {
        Path documentPath = toPath(filename);
        DocumentParser documentParser = new TextDocumentParser();
        Document document = FileSystemDocumentLoader.loadDocument(documentPath, documentParser);
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);
        return splitter.split(document);
    }

    private static Path toPath(String fileName) {
        try {
            URL fileUrl = ContentRetrieverFactory.class.getClassLoader().getResource(fileName);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}
