package org.acme;

import java.util.Map;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.PromptTemplate;

public class JokenpoBot {
    
    private ChatLanguageModel chatModel;
    private ChatMemory chatMemory;

    public JokenpoBot(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
        chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        chatMemory.add(SystemMessage.from("""
            # BEGIN JOKENPO RULES
            1. Two players choose between Scisor, Paper and Rock;
            2. Scisor wins against Paper;
            3. Paper wins against Rock;
            4. Rock wins against Scisor;
            5. If both players choose the same option it's considered Draw;
            # END JOKENPO RULES
            
            # BEGIN AI INSTRUCTIONS
            CHATBOT will play against USER.
            CHATBOT randomly reply with Scisor, Paper or Rock everytime USER input Scisor, Paper or Rock.
            CHATBOT reply normally otherwise.
            # END AI INSTRUCTIONS
            """));
    }

    public String chat(String message){

        chatMemory.add(UserMessage.from("USER", message));

        var result = chatModel
                        .generate(chatMemory.messages())
                        .content();

        chatMemory.add(result);

        chatMemory.messages().stream().map(m -> m.toString()).forEach(System.out::println);

        return result.text();
    }
    
}
