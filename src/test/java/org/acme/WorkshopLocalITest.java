package org.acme;

import io.quarkus.logging.Log;
import org.acme.assistants.DebuggerAssistant;
import org.acme.assistants.ChatAssistant;
import org.acme.assistants.DocumentAssistant;
import org.acme.assistants.SearchAssistant;
import org.acme.factories.AiModelFactory;
import org.acme.factories.ContentRetrieverFactory;
import org.acme.factories.EmbeddingFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class WorkshopLocalITest {
    @Test
    @Disabled
    void test_1_Model() {

        var chatModel = AiModelFactory.createLocalChatModel();

        var result = chatModel.generate("Write a Java hello world");

        Log.info(result);
    }

    @Test
    @Disabled
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
    @Disabled
    void test_3_NoMemory() {

        var chatModel = AiModelFactory.createLocalChatModel();

        Log.info(chatModel.generate("I'm a Java programmer"));
        Log.info(chatModel.generate("In which language do I program?"));
    }

    @Test
    @Disabled
    void test_3_Memory() {

        var chatModel = AiModelFactory.createLocalChatModel();
        var chatAssistant = new ChatAssistant(chatModel);

        Log.info(chatAssistant.chat("I'm a Java programmer"));
        Log.info(chatAssistant.chat("In which language do I program?"));
    }

    @Test
    @Disabled
    void test_4_RAG() {

        var chatModel = AiModelFactory.createLocalChatModel();
        var embeddingModel = EmbeddingFactory.createEmbeddingModel();
        var embeddingStore = EmbeddingFactory.createEmbeddingStore();
        var fileContentRetriever = ContentRetrieverFactory.createFileContentRetriever(
                embeddingModel,
                embeddingStore,
                "news.pdf");

        var documentAssistant = new DocumentAssistant(chatModel, fileContentRetriever);

        String result = documentAssistant.chat("What the news says about AI?");

        Log.info(result);
    }

    @Test
    @Disabled
    void test_5_Tools() {

        var chatModel = AiModelFactory.createLocalChatModel();
        var searchAssistant = new SearchAssistant(chatModel);

        String result = searchAssistant.chat("What google says about Java and Generative AI?");

        Log.info(result);
    }
}
