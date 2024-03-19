package org.acme;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SearchBot {

    private ChatLanguageModel chatModel;

    public SearchBot(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
    }

    class SearchTools {

        @Tool("Searches Google for relevant URLs, given the query")
        public List<String> searchGoogle(@P("search query") String query) {
            var googleSearchURL = "https://www.google.com/search?q="+query.replace(" ", "+");

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

    interface GoogleSearchAssistant {
        String chat(String userMessage);
    }

    public String chat(String userMessage) {
        var assistant = AiServices.builder(GoogleSearchAssistant.class)
                .chatLanguageModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .tools(new SearchTools())
                .build();

        return assistant.chat(userMessage);
    }

}
