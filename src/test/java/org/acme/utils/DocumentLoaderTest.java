package org.acme.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DocumentLoaderTest {

    @Test
    void getResourceDocument_xlsx() {
        var document = DocumentLoader.getResourceDocument("test.xlsx");
        Assertions.assertNotNull(document);
        Assertions.assertTrue(document.text().contains("ID"));
        Assertions.assertTrue(document.text().contains("COLUMN1"));
        Assertions.assertTrue(document.text().contains("COLUMN2"));
        Assertions.assertTrue(document.text().contains("abc"));
        Assertions.assertTrue(document.text().contains("qwe"));
        Assertions.assertTrue(document.text().contains("123"));
        Assertions.assertTrue(document.text().contains("456"));
    }

    @Test
    void getResourceDocument_pdf() {
        var document = DocumentLoader.getResourceDocument("test.pdf");
        Assertions.assertNotNull(document);
        Assertions.assertTrue(document.text().contains("test PDF document."));
        Assertions.assertTrue(document.text().contains("you can read this"));
    }
}