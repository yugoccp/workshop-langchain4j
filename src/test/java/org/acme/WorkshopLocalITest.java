package org.acme;

import org.acme.assistants.DebuggerAssistant;
import org.acme.assistants.ChatAssistant;
import org.acme.assistants.DocumentAssistant;
import org.acme.assistants.SearchAssistant;
import org.acme.utils.AiModelFactory;
import org.acme.utils.ContentRetrieverFactory;
import org.acme.utils.DocumentLoader;
import org.acme.utils.EmbeddingFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WorkshopLocalITest {

    private static final Logger logger = LoggerFactory.getLogger(WorkshopLocalITest.class);

    @Test
    @Disabled
    void test_1_Model() {

        var chatModel = AiModelFactory.createLocalChatModel();

        var result = chatModel.generate("Write a Java hello world");

        logger.info(result);
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

        logger.info(result);
    }


    @Test
    @Disabled
    void test_3_NoMemory() {

        var chatModel = AiModelFactory.createLocalChatModel();

        var prompt1 = "I'm a Java programmer";
        logger.info(prompt1);
        logger.info(chatModel.generate(prompt1));

        var prompt2 = "In which language do I program?";
        logger.info(prompt2);
        logger.info(chatModel.generate(prompt2));
    }

    @Test
    @Disabled
    void test_3_Memory() {

        var chatModel = AiModelFactory.createLocalChatModel();
        var chatAssistant = new ChatAssistant(chatModel);

        var prompt1 = "I'm a Java programmer";
        logger.info(prompt1);
        logger.info(chatAssistant.chat(prompt1));

        var prompt2 = "In which language do I program?";
        logger.info(prompt2);
        logger.info(chatAssistant.chat(prompt2));
    }

    @Test
    @Disabled
    void test_4_RAG() {

        var chatModel = AiModelFactory.createLocalChatModel();
        var embeddingModel = EmbeddingFactory.createEmbeddingModel();
        var embeddingStore = EmbeddingFactory.createEmbeddingStore();
        var document = DocumentLoader.getResourceDocument("news.pdf");
        var contentRetriever = ContentRetrieverFactory.createContentRetriever(
                embeddingModel,
                embeddingStore,
                document);

        var documentAssistant = new DocumentAssistant(chatModel, contentRetriever);

        String result = documentAssistant.chat("What the news says about AI?");

        logger.info(result);
    }

    @Test
    @Disabled
    void test_5_Tools() {

        var chatModel = AiModelFactory.createLocalChatModel();
        var searchAssistant = new SearchAssistant(chatModel);

        String result = searchAssistant.chat("Search latest articles about Java and Generative AI in Google");

        logger.info(result);
    }
}
