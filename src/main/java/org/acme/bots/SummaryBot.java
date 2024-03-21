package org.acme.bots;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;

public class SummaryBot {
    
    private ChatLanguageModel chatModel;
    private ChatMemory chatMemory;

    public SummaryBot(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
        chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        chatMemory.add(SystemMessage.from("""
                   As an AI designed to summarize, please follow this simple process:                                                                                                                                                            
                   1. Confirm each piece of information provided by the user with a simple 'Ok.'
                   2. After the user types 'Summarize,' generate a coherent and concise summary of all the information provided by the user.
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
