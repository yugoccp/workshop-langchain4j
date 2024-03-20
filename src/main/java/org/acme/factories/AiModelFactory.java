package org.acme.factories;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class AiModelFactory {


    public static ChatLanguageModel createChatModel() {
        return createLocalChatModel();
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
