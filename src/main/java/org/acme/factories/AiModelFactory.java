package org.acme.factories;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class AiModelFactory {

    public enum AiModelSource {
        OPEN_AI,
        LOCAL
    }

    public static ChatLanguageModel createChatModel(AiModelSource source) {
        return switch(source) {
            case OPEN_AI -> createOpenAIChatModel();
            case LOCAL -> createLocalChatModel();
        };
    }

    public static ChatLanguageModel createLocalChatModel() {
        return OpenAiChatModel.builder()
        .baseUrl("http://localhost:1234/v1")
        .apiKey("ignore")
        .build();
    }

    public static ChatLanguageModel createOpenAIChatModel() {
        return OpenAiChatModel.withApiKey(System.getenv("OPENAI_KEY"));
    }
}
