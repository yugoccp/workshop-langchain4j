package org.acme;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;

public class NumberBot {
    
    private ChatLanguageModel chatModel;
    private ChatMemory chatMemory;

    public NumberBot(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
        chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        chatMemory.add(SystemMessage.from("""
                You only accept a valid number or 'Result' as input.
                For invalid input you will reply with 'Invalid input'
                For valid numbers, you reply with 'Ok'
                For 'Result', you will tell me the biggest number between all the numbers I gave to you.
                """));
    }

    public String chat(String message){

        chatMemory.add(UserMessage.from(message));

        var aiMessage = chatModel
                .generate(chatMemory.messages())
                .content();

        chatMemory.add(aiMessage);

        return aiMessage.text();
    }
    
}
