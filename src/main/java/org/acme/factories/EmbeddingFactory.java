package org.acme.factories;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

public class EmbeddingFactory {

    private EmbeddingFactory() {
        throw new IllegalStateException("Factory class shouldn't be instantiated");
    }

    public static EmbeddingModel createEmbeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    public static EmbeddingStore<TextSegment> createEmbeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

}
