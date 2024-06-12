package org.acme.assistants;

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
        var debuggerTemplate = PromptTemplate.from("""
                   You're a Java Developer expert.
                   You can analyze and tell if a Java code snippet have some syntax error.
                   If you identify an error, you do reply describing the issue and an example of correct syntax.
                   If no error is identified, reply with "It's good to compile! :)"

                   Can you identify any bugs in the Java code snippet bellow?
                   ```java
                   {{codeSnippet}}
                   ```
                """);

        // Replace template variables with the movieName.
        var userMessage = debuggerTemplate.apply(Map.of("codeSnippet", codeSnippet)).toUserMessage();

        // Send the template result to the model and return the model response.
        return chatModel.generate(userMessage).content().text();
    }
}
