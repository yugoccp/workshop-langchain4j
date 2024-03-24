package org.acme.bots;

import java.util.Map;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.PromptTemplate;

public class DebuggerAssistant {
    
    private ChatLanguageModel chatModel;

    public DebuggerAssistant(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
    }

    public String generate(String codeSnippet) {

        // Create a PromptTemplate for model instructions.
        var emojiTemplate = PromptTemplate.from("""
           Can you identify any bugs in this Java code snippet? 
           {{codeSnippet}}
        """);

        // Replace template variables with the movieName.
        var userMessage = emojiTemplate.apply(Map.of("codeSnippet", codeSnippet)).toUserMessage();

        // Send the template result to the model and return the model response.
        return chatModel.generate(userMessage).content().text();
    }
}
