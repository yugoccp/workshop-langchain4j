package org.acme;

import io.quarkus.logging.Log;
import org.acme.assistants.ChatAssistant;
import org.acme.assistants.DebuggerAssistant;
import org.acme.assistants.DocumentAssistant;
import org.acme.assistants.SearchAssistant;
import org.acme.factories.AiModelFactory;
import org.acme.factories.ContentRetrieverFactory;
import org.acme.factories.EmbeddingFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class WorkshopOpenAiITest {
    @Test
    @Disabled
    void test_1_Model() {

        var chatModel = AiModelFactory.createOpenAIChatModel();

        var result = chatModel.generate("Write a Java hello world");

        Log.info(result);
    }

    @Test
    @Disabled
    void test_2_PromptTemplate() {

        var chatModel = AiModelFactory.createOpenAIChatModel();
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

        var chatModel = AiModelFactory.createOpenAIChatModel();

        var prompt1 = "I'm a Java programmer";
        Log.info(prompt1);
        Log.info(chatModel.generate(prompt1));

        var prompt2 = "In which language do I program?";
        Log.info(prompt2);
        Log.info(chatModel.generate(prompt2));
    }

    @Test
    @Disabled
    void test_3_Memory() {

        var chatModel = AiModelFactory.createOpenAIChatModel();
        var chatAssistant = new ChatAssistant(chatModel);

        var prompt1 = "I'm a Java programmer";
        Log.info(prompt1);
        Log.info(chatAssistant.chat(prompt1));

        var prompt2 = "In which language do I program?";
        Log.info(prompt2);
        Log.info(chatAssistant.chat(prompt2));
    }

    @Test
    @Disabled
    void test_4_RAG() {

        var chatModel = AiModelFactory.createOpenAIChatModel();
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

        var chatModel = AiModelFactory.createOpenAIChatModel();
        var searchAssistant = new SearchAssistant(chatModel);

        String result = searchAssistant.chat("What google says about Java and Generative AI?");

        Log.info(result);
    }
}
