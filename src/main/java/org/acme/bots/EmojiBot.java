package org.acme.bots;

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
            From the movie '{{movieName}}', generate a short plot only using emojis
            that illustrates remarkable objects or moments of the movie.
        """);
        var userMessage = emojiTemplate.apply(Map.of("movieName", movieName)).toUserMessage();
        return chatModel.generate(userMessage).content().text();
    }
}
