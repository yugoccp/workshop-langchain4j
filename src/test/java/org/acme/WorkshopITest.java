package org.acme;

import io.quarkus.logging.Log;
import org.acme.bots.SummaryBot;
import org.acme.bots.DocumentBot;
import org.acme.bots.EmojiBot;
import org.acme.bots.SearchBot;
import org.acme.factories.AiModelFactory;
import org.acme.factories.ContentRetrieverFactory;
import org.acme.factories.EmbeddingFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class WorkshopITest {

    @Test
    void test_1_ModelLocal() {
        
        var chatModel = AiModelFactory.createChatModel(AiModelFactory.AiModelSource.OPEN_AI);
        
        var result = chatModel.generate("hello");
        
        Log.info(result);
    }

    @Test
    void test_1_ModelOpenAi() {

        var chatModel = AiModelFactory.createChatModel(AiModelFactory.AiModelSource.OPEN_AI);

        var result = chatModel.generate("hello");

        Log.info(result);
    }

    @Test
    void test_2_PromptTemplate() {
        
        var chatModel = AiModelFactory.createChatModel(AiModelFactory.AiModelSource.OPEN_AI);
        var emojiBot = new EmojiBot(chatModel);

        var result = emojiBot.generate("Titanic");

        Log.info(result);
    }

    @Test
    void test_3_Memory() {
        
        var chatModel = AiModelFactory.createChatModel(AiModelFactory.AiModelSource.LOCAL);
        var memoryBot = new SummaryBot(chatModel);

        Log.info(memoryBot.chat("I've build s Java application integrated with LLM"));
        Log.info(memoryBot.chat("I've learned from a workshop in a large technology conference"));
        Log.info(memoryBot.chat("Can't wait to build world changing solutions with it!"));
        Log.info(memoryBot.chat("Summarize"));
    }

    @Test
    void test_4_RAG() {
        
        var chatModel = AiModelFactory.createChatModel(AiModelFactory.AiModelSource.LOCAL);
        var embeddingModel = EmbeddingFactory.createEmbeddingModel();
        var embeddingStore = EmbeddingFactory.createEmbeddingStore();
        var fileContentRetriever = ContentRetrieverFactory.createFileContentRetriever(
                                        embeddingModel,
                                        embeddingStore,
                                        "wired.rss");

        var documentBot = new DocumentBot(chatModel, fileContentRetriever);

        String result = documentBot.chat("What it says about Apple?");

        Log.info(result);
    }

    @Test
    void test_5_Tools() {

        var chatModel = AiModelFactory.createChatModel(AiModelFactory.AiModelSource.OPEN_AI);
        var agentBot = new SearchBot(chatModel);

        String result = agentBot.chat("What google says about Generative AI?");

        Log.info(result);
    }
}
