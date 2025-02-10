package org.acme.utils;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.apache.commons.io.FilenameUtils.getExtension;

public class DocumentLoader {

    private static final Logger logger = LoggerFactory.getLogger(DocumentLoader.class);

    public static Document getContent(Path filePath, String filename) {
        var fileExt = getExtension(filename);
        logger.debug("Loading file from {}", filePath);

        try {
            return switch (fileExt) {
                case "pdf" -> FileSystemDocumentLoader.loadDocument(filePath, new ApachePdfBoxDocumentParser());
                case "xlsx" -> FileSystemDocumentLoader.loadDocument(filePath, new ApacheTikaDocumentParser());
                default -> FileSystemDocumentLoader.loadDocument(filePath);
            };
        } catch (Exception e) {
            logger.error("Error while loading file from {}", filePath, e);
            return null;
        }
    }

    public static Document getResourceDocument(String filename) {
        var path = getResourcePath(filename);
        return getContent(path,  filename);
    }

    public static Path getResourcePath(String fileName) {
        try {
            var fileUrl = DocumentLoader.class.getClassLoader().getResource(fileName);
            var filePath = Objects.requireNonNull(fileUrl).toURI();
            return Paths.get(filePath);
        } catch (URISyntaxException e) {
            logger.error("Cannot read file: {}", fileName);
            throw new IllegalStateException(e);
        }
    }

}
