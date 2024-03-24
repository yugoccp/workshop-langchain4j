package org.acme.app.prompt;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class PromptService {
    private static final String COMMA_DELIMITER = ";";
    private static final String DEFAULT_FILE = "prompts.csv";

    public List<PromptRecord> getAllPrompts() {
        var promptDataList = readCsv(DEFAULT_FILE).stream()
                .map(row -> new PromptRecord(row[0], row[1]))
                .toList();
        return promptDataList;
    }

    public PromptRecord getPrompt(String promptName) {
        return getAllPrompts().stream()
                .filter(p -> promptName.equals(p.name()))
                .findFirst()
                .orElseThrow();
    }

    private List<String[]> readCsv(String filename) {
        File file = getFile(filename);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            var rows = new ArrayList<String[]>();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                rows.add(values);
            }
            return rows;
        } catch (Exception e) {
            Log.error("Error loading CSV file:" + e.getMessage());
        }

        return Collections.emptyList();
    }

    private static File getFile(String filename) {
        var fileUrl = PromptService.class.getClassLoader().getResource(filename);
        File file = null;
        try {
            file = new File(fileUrl.toURI());
        } catch (URISyntaxException e) {
            Log.error("Error to open file: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return file;
    }
}
