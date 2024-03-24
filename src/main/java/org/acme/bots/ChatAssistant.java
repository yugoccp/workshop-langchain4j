package org.acme.bots;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;

public class ChatAssistant {
    
    private ChatLanguageModel chatModel;
    private ChatMemory chatMemory;

    public ChatAssistant(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
        chatMemory = MessageWindowChatMemory.withMaxMessages(10);
    }

    public String chat(String message){

        // Add user message to chatMemory
        chatMemory.add(UserMessage.from(message));

        // Send all the chatMemory messages to chatModel
        var aiMessage = chatModel
                .generate(chatMemory.messages())
                .content();

        // Add model response to chatMemory to give whole context for the model in the next Prompt
        chatMemory.add(aiMessage);

        // Return model response text
        return aiMessage.text();
    }
    
}
