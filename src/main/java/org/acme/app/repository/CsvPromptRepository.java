package org.acme.app.repository;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.app.dto.PromptDto;
import org.acme.utils.DocumentLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ApplicationScoped
public class CsvPromptRepository implements PromptRepository {

    private static final Logger logger = LoggerFactory.getLogger(CsvPromptRepository.class);

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
        var csvFile = DocumentLoader.getResourceDocument(filename).text();
        logger.debug("Successfully read file: {}", filename);
        return Stream.of(csvFile.split("\n"))
                .map(line -> line.split(CSV_DELIMITER))
                .toList();
    }
}
