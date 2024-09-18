package org.acme.app.repository;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.app.dto.PromptDto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CsvPromptRepository implements PromptRepository {

    private static final String CSV_DELIMITER = ";";
    private static final String DEFAULT_PROMPT_FILE = "prompts.csv";

    private final String promptFilePath = Optional.ofNullable(System.getenv("PROMPT_PATH")).orElse(DEFAULT_PROMPT_FILE);

    public List<PromptDto> findAll() {
        return readCsv(promptFilePath).stream()
                .map(row -> new PromptDto(row[0], row[1]))
                .toList();
    }

    public PromptDto findPrompt(String promptName) {
        return findAll().stream()
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
                String[] values = line.split(CSV_DELIMITER);
                rows.add(values);
            }
            return rows;
        } catch (Exception e) {
            Log.error("Error loading CSV file:" + e.getMessage());
        }

        return Collections.emptyList();
    }

    private static File getFile(String filename) {
        var fileUrl = CsvPromptRepository.class.getClassLoader().getResource(filename);
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
