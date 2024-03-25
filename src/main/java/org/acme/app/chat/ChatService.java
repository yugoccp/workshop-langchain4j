package org.acme.app.chat;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.app.prompt.PromptService;
import org.acme.bots.ChatAssistant;
import org.acme.factories.AiModelFactory;

import java.util.List;

@ApplicationScoped
public class ChatService {

    @Inject
    private PromptService promptService;
    private ChatLanguageModel chatModel;
    private ChatAssistant chatAssistant;

    public void initChat(String promptName, ChatModelTypeEnum source) {
        var prompt = promptService.getPrompt(promptName);
        chatModel = switch(source) {
            case OPEN_AI -> AiModelFactory.createOpenAIChatModel();
            case LOCAL -> AiModelFactory.createLocalChatModel();
        };
        chatAssistant = new ChatAssistant(chatModel, prompt.text());
    }

    public List<ChatMessage> getMessages() {
        return chatAssistant.getMessages();
    }

    public void chat(String message) {
        chatAssistant.chat(message);
    }
}
