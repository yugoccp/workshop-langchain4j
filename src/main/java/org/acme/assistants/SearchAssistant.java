package org.acme.assistants;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import io.quarkus.logging.Log;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SearchAssistant {

    private GoogleSearchAssistant searchAssistant;

    interface GoogleSearchAssistant {
        String chat(String userMessage);
    }

    private class SearchTools {
        @Tool("Use Google to search for relevant URLs, given the query")
        public List<String> searchGoogleTool(@P("search query") String query) {
            if (query != null && !query.isEmpty()) {
                return getGoogleResults(query);
            }

            return Collections.emptyList();
        }
    }

    public SearchAssistant(ChatLanguageModel chatModel) {
        this.searchAssistant = AiServices.builder(GoogleSearchAssistant.class)
                .chatLanguageModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .tools(new SearchTools())
                .build();
    }

    public String chat(String userMessage) {
        return searchAssistant.chat(userMessage);
    }

    private static List<String> getGoogleResults(@NotNull String query) {
        var googleSearchURL = "https://www.google.com/search?q=" + query.replace(" ", "+");

        Document doc = null;
        try {
            System.out.println("Search at google: " + googleSearchURL);
            doc = Jsoup.connect(googleSearchURL).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var titles = doc
                .select("div[data-snhf]")
                .eachText();

        var descriptions = doc
                .select("div[data-sncf]")
                .eachText();

        return IntStream.range(0, titles.size())
                .mapToObj(i -> List.of(titles.get(i), descriptions.get(i)))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

}
