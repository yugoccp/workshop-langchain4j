package org.acme;

import io.quarkus.logging.Log;
import org.acme.bots.DebuggerAssistant;
import org.acme.bots.ChatAssistant;
import org.acme.bots.DocumentAssistant;
import org.acme.bots.SearchAssistant;
import org.acme.factories.AiModelFactory;
import org.acme.factories.ContentRetrieverFactory;
import org.acme.factories.EmbeddingFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class WorkshopLocalITest {
    @Test
    void test_1_Model() {
        
        var chatModel = AiModelFactory.createLocalChatModel();
        
        var result = chatModel.generate("hello world!");
        
        Log.info(result);
    }

    @Test
    void test_2_PromptTemplate() {
        
        var chatModel = AiModelFactory.createLocalChatModel();
            var debuggerAssistant = new DebuggerAssistant(chatModel);

        var result = debuggerAssistant.generate("""
                public static void main(String[] args) {
                    System.out.println("Hello World!")
                }
                """);

        Log.info(result);
    }

    @Test
    void test_3_Memory() {
        
        var chatModel = AiModelFactory.createLocalChatModel();
        var chatAssistant = new ChatAssistant(chatModel);

        Log.info(chatAssistant.chat("Give me short description of observer pattern"));
        Log.info(chatAssistant.chat("Give me a smallest example in Java, focus on conveying the core idea only"));
    }

    @Test
    void test_4_RAG() {
        
        var chatModel = AiModelFactory.createLocalChatModel();
        var embeddingModel = EmbeddingFactory.createEmbeddingModel();
        var embeddingStore = EmbeddingFactory.createEmbeddingStore();
        var fileContentRetriever = ContentRetrieverFactory.createFileContentRetriever(
                                        embeddingModel,
                                        embeddingStore,
                                        "hckrnews.html");

        var documentAssistant = new DocumentAssistant(chatModel, fileContentRetriever);

        String result = documentAssistant.chat("What it says about Apple?");

        Log.info(result);
    }

    @Test
    void test_5_Tools() {

        var chatModel = AiModelFactory.createLocalChatModel();
        var searchAssistant = new SearchAssistant(chatModel);

        String result = searchAssistant.chat("What google says about Java and Generative AI?");

        Log.info(result);
    }
}