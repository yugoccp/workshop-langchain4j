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

        // Create a PromptTemplate for model instructions.
        var emojiTemplate = PromptTemplate.from("""
            From the movie '{{movieName}}', generate a short plot only using emojis
            that illustrates remarkable objects or moments of the movie.
        """);

        // Replace template variables with the movieName.
        var userMessage = emojiTemplate.apply(Map.of("movieName", movieName)).toUserMessage();

        // Send the template result to the model and return the model response.
        return chatModel.generate(userMessage).content().text();
    }
}
