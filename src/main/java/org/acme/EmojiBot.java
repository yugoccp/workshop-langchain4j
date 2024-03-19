package org.acme;

import java.util.Map;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.PromptTemplate;

public class EmojiBot {
    
    private ChatLanguageModel chatModel;

    public EmojiBot(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
    }

    public String generate(String movieName) {
        var emojiTemplate = PromptTemplate.from("""
            From the movie '{{movieName}}':
            1. Generate a summary of the movie plot.
            2. Identify the remarkable objects and moments.
            3. Translate the plot to emojis using the identified objects and moments.
        """);
        var userMessage = emojiTemplate.apply(Map.of("movieName", movieName)).toUserMessage();
        return chatModel.generate(userMessage).content().text();
    }
}
