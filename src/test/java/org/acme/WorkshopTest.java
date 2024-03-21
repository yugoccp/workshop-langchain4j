package org.acme;

import org.acme.bots.SummaryBot;
import org.acme.bots.DocumentBot;
import org.acme.bots.EmojiBot;
import org.acme.bots.SearchBot;
import org.acme.factories.AiModelFactory;
import org.acme.factories.EmbeddingFactory;
import org.junit.jupiter.api.Test;

class WorkshopTest {
    
    @Test
    void test_1_Model() {
        
        var chatModel = AiModelFactory.createChatModel();
        
        var result = chatModel.generate("hello");
        
        System.out.println(result);
    }

    @Test
    void test_2_PromptTemplate() {
        
        var chatModel = AiModelFactory.createChatModel();
        var emojiBot = new EmojiBot(chatModel);

        var result = emojiBot.generate("Titanic");

        System.out.println(result);
    }

    @Test
    void test_3_Memory() {
        
        var chatModel = AiModelFactory.createChatModel();
        var memoryBot = new SummaryBot(chatModel);

        System.out.println(memoryBot.chat("I've build s Java application integrated with LLM"));
        System.out.println(memoryBot.chat("I've learned from a workshop in a large technology conference"));
        System.out.println(memoryBot.chat("Can't wait to build world changing solutions with it!"));
        System.out.println(memoryBot.chat("Summarize"));
    }

    @Test
    void test_4_RAG() {
        
        var chatModel = AiModelFactory.createChatModel();
        var embeddingModel = EmbeddingFactory.createEmbeddingModel();
        var embeddingStore = EmbeddingFactory.createEmbeddingStore();

        var documentBot = new DocumentBot(chatModel, embeddingModel, embeddingStore);

        String result = documentBot.chat("news.rss", "What's new at TikTok?");

        System.out.println(result);
    }

    @Test
    void test_5_Tools() {

        var chatModel = AiModelFactory.createChatModel();
        var agentBot = new SearchBot(chatModel);

        String result = agentBot.chat("What google says about Generative AI?");

        System.out.println(result);
    }
}
