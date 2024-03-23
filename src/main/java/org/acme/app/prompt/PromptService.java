package org.acme.app;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class PromptService {

    private static final String COMMA_DELIMITER = ";";

    private List<PromptItem> getPromptsFromCsvFile() throws IOException {
        var promptItemList = new ArrayList<PromptItem>();
        try (BufferedReader br = new BufferedReader(new FileReader("prompts.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                promptItemList.add(new PromptItem(values[0], values[1]));
            }
        } catch (Exception e) {
            System.out.println("Error loading CSV file:" + e.getMessage());
            throw e;
        }

        return promptItemList;
    }

    public List<PromptItem> getAllPrompts() {

        List<PromptItem> prompts;
        try {
            prompts = getPromptsFromCsvFile();
        } catch (IOException e) {
            prompts = Collections.emptyList();
        }

        return prompts;
    }
}
