package org.acme.app.prompt;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.factories.ContentRetrieverFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PromptService {

    private static final String COMMA_DELIMITER = ";";
    private static final String FILENAME = "prompts.csv";

    public List<PromptData> getAllPrompts() {
        var promptDataList = readCsv(FILENAME).stream()
                .map(row -> new PromptData(row[0], row[1]))
                .toList();
        return promptDataList;
    }

    public String getPrompt(String promptName) {
        if (promptName != null) {
            return getAllPrompts().stream()
                    .filter(p -> promptName.equals(p.name))
                    .map(p -> p.value)
                    .findFirst()
                    .orElse("");
        }

        Log.error("Cannot find prompt with name: " + promptName);
        return "";
    }

    public void removePrompt(String promptName) {
        var promptDataList = getAllPrompts();
        var csvRows = promptDataList.stream()
                .filter(pd -> !promptName.equals(pd.name))
                .map(pd -> String.format("%s; %s\n", pd.name, pd.value))
                .toList();

        writeCsv(FILENAME, csvRows);
    }

    private void writeCsv(String filename, List<String> rows) {
        File file = getFile(filename);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String row : rows) {
                bw.write(row);
            }
        } catch (Exception e) {
            Log.error("Error loading CSV file:" + e.getMessage());
        }
    }

    private List<String[]> readCsv(String filename) {
        var result = new ArrayList<String[]>();
        File file = getFile(filename);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                result.add(values);
            }
        } catch (Exception e) {
            Log.error("Error loading CSV file:" + e.getMessage());
        }

        return result;
    }

    private static File getFile(String filename) {
        var fileUrl = ContentRetrieverFactory.class.getClassLoader().getResource(filename);
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
