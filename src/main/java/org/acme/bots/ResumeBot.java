package org.acme.bots;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;

public class ResumeBot {
    
    private ChatLanguageModel chatModel;
    private ChatMemory chatMemory;

    public ResumeBot(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
        chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        chatMemory.add(SystemMessage.from("""
                You are an AI skilled in analyzing user-provided information to craft professional resumes. 
                When a user provides you with descriptions of their skills, work experience, education, and any other relevant details
                Acknowledge each entry with a simple 'Ok.' 
                Once the user requests a summary, synthesize all the acknowledged information into a coherent 
                and concise resume summary that highlights the user's qualifications and strengths.
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
