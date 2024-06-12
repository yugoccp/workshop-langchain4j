package org.acme.factories;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.time.Duration;
import java.util.Optional;

public class AiModelFactory {

    private AiModelFactory() {
        throw new IllegalStateException("Factory class shouldn't be instantiated");
    }

    public static ChatLanguageModel createLocalChatModel() {
        return OpenAiChatModel.builder()
                .baseUrl("http://localhost:1234/v1")
                .apiKey("ignore")
                .logRequests(true)
                .timeout(Duration.ofSeconds(300))
                .build();
    }

    public static ChatLanguageModel createOpenAIChatModel() {
        var openAiKey = Optional.ofNullable(System.getenv("OPENAI_KEY")).orElse("demo");
        return OpenAiChatModel.builder()
                .apiKey(openAiKey)
                .logRequests(true)
                .build();
    }
}
