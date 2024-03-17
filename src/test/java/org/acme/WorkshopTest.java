package org.acme;

import org.junit.jupiter.api.Test;

import java.io.File;

class WorkshopTest {
    
    // @Test
    void testModel() {
        
        var chatModel = ChatModelFactory.createModel();
        
        var result = chatModel.generate("hello");
        
        System.out.println(result);
    }

    // @Test
    void testPrompt() {
        
        var chatModel = ChatModelFactory.createModel();
        var emojiMovieBot = new EmojiMovieBot(chatModel);

        var result = emojiMovieBot.generate("Titanic");

        System.out.println(result);
    }

    //@Test
    void testMemory() {
        
        var chatModel = ChatModelFactory.createModel();
        var jokenpoBot = new JokenpoBot(chatModel);


        var myOption = "Rock";
        var botOption = jokenpoBot.chat(myOption);
        var result = jokenpoBot.chat("Who won and why?");

        System.out.println("USER: "+ myOption);
        System.out.println("JOKENPOBOT: " + botOption);
        System.out.println(result);
    }

    @Test
    void testRAG() {
        
        var chatModel = ChatModelFactory.createModel();
        var documentBot = new DocumentBot(chatModel);

        documentBot.loadFile("news.html");
        String result = documentBot.chat("Qual a boa de hoje?");

        System.out.println(result);
    }
}
