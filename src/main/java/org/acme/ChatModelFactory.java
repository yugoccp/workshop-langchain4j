package org.acme;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class ChatModelFactory {


    public static ChatLanguageModel createModel() {
        return createOpenAIModel();
    }

    public static ChatLanguageModel createLocalModel() {
        return OpenAiChatModel.builder()
        .baseUrl("http://localhost:1234/v1")
        .apiKey("ignore")
        .build();
    }

    public static ChatLanguageModel createOpenAIModel() {
        return OpenAiChatModel.withApiKey(System.getenv("OPENAI_KEY"));
    }
}
