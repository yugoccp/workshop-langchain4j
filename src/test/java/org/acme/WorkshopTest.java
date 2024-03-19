package org.acme;

import org.junit.jupiter.api.Test;

class WorkshopTest {
    
    @Test
    void test_1_Model() {
        
        var chatModel = ChatModelFactory.createModel();
        
        var result = chatModel.generate("hello");
        
        System.out.println(result);
    }

    @Test
    void test_2_PromptTemplate() {
        
        var chatModel = ChatModelFactory.createModel();
        var emojiMovieBot = new EmojiBot(chatModel);

        var result = emojiMovieBot.generate("Titanic");

        System.out.println(result);
    }

    @Test
    void test_3_Memory() {
        
        var chatModel = ChatModelFactory.createModel();
        var memoryBot = new NumberBot(chatModel);

        memoryBot.chat("33");
        memoryBot.chat("120");
        memoryBot.chat("3");
        memoryBot.chat("7");

        var result = memoryBot.chat("Result");
        System.out.println(result);
    }

    @Test
    void test_4_RAG() {
        
        var chatModel = ChatModelFactory.createModel();
        var documentBot = new DocumentBot(chatModel, "news.rss");

        String result = documentBot.chat("What's new at TikTok?");

        System.out.println(result);
    }

    @Test
    void test_5_Tools() {

        var chatModel = ChatModelFactory.createModel();
        var agentBot = new SearchBot(chatModel);

        String result = agentBot.chat("What google says about Generative AI?");

        System.out.println(result);
    }
}
