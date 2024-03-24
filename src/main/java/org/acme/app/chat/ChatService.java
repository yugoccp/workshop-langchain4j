package org.acme.app.chat;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.factories.AiModelFactory;

import java.util.List;

@ApplicationScoped
public class ChatService {
    private ChatMemory chatMemory;

    public void createChat(String selectedPrompt) {
        chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        chatMemory.add(SystemMessage.from(selectedPrompt));
    }

    public List<ChatMessage> getMessages() {
        return chatMemory.messages();
    }

    public void chat(String message, ChatAiModelTypeEnum source) {
        var chatModel = createChatModel(source);
        chatMemory.add(UserMessage.from(message));
        var response = chatModel.generate(chatMemory.messages());
        chatMemory.add(response.content());
    }

    private ChatLanguageModel createChatModel(ChatAiModelTypeEnum source) {
        return switch(source) {
            case OPEN_AI -> AiModelFactory.createOpenAIChatModel();
            case LOCAL -> AiModelFactory.createLocalChatModel();
        };
    }
}
