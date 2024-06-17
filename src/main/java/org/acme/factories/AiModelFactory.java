package org.acme.factories;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.time.Duration;
import java.util.Optional;

public class AiModelFactory {

    private AiModelFactory() {
        throw new IllegalStateException("Factory class shouldn't be instantiated");
    }

    public static ChatLanguageModel createLocalChatModel() {
        var localUrl = Optional.ofNullable(System.getenv("LOCAL_LLM")).orElse("http://localhost:11434");
        return OllamaChatModel.builder()
                .baseUrl(localUrl)
                .modelName("gemma:2b")
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
