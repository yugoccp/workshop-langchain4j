package org.acme.utils;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;

public class ContentRetrieverFactory {

    private ContentRetrieverFactory() {
        throw new IllegalStateException("Factory class shouldn't be instantiated");
    }

    public static ContentRetriever createContentRetriever(EmbeddingModel embeddingModel,
                                                          EmbeddingStore<TextSegment> embeddingStore, String content) {
        // Transform single file content into chunks of text segments.;
        var splitter = DocumentSplitters.recursive(500, 100);
        var segments = splitter.split(Document.from(content));

        // Transform segments into embeddings (vectors)
        var embeddings = embeddingModel.embedAll(segments).content();

        // Store embeddings with the corresponding segments
        embeddingStore.addAll(embeddings, segments);

        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(7)
                .minScore(0.5)
                .build();
    }

}
