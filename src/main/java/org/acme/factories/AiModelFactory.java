package org.acme.factories;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.time.Duration;

public class AiModelFactory {
    public static ChatLanguageModel createLocalChatModel() {
        return OpenAiChatModel.builder()
                .baseUrl(System.getenv("LOCAL_URL"))
                .apiKey("ignore")
                .logRequests(true)
                .timeout(Duration.ofSeconds(300))
                .build();
    }

    public static ChatLanguageModel createOpenAIChatModel() {
        return OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_KEY"))
                .logRequests(true)
                .build();
    }
}
