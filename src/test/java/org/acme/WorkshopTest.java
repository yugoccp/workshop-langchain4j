package org.acme;

import org.acme.bots.ResumeBot;
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
        var memoryBot = new ResumeBot(chatModel);

        System.out.println(memoryBot.chat("My name is John Doe, and I have 38 years old"));
        System.out.println(memoryBot.chat("I'm a Software Engineer with more than 8 years of experience"));
        System.out.println(memoryBot.chat("I've delivered several Java Web applications using Quarkus for different customers from Natural Resources to Financial businesses"));
        System.out.println(memoryBot.chat("I'm data oriented and have strong problem solving skills, also play very well with other team members"));
        System.out.println(memoryBot.chat("Summarize for me"));
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
