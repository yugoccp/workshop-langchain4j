package org.acme.utils;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.apache.commons.io.FilenameUtils.getExtension;

public class DocumentLoader {

    private static final Logger logger = LoggerFactory.getLogger(DocumentLoader.class);

    public static Document getContent(Path filePath) {
        var fileExt = getExtension(filePath.getFileName().toString());
        if (fileExt.equals("pdf")) {
            return FileSystemDocumentLoader.loadDocument(filePath, new ApachePdfBoxDocumentParser());
        } else {
            return FileSystemDocumentLoader.loadDocument(filePath);
        }
    }

    public static Document getResourceDocument(String filename) {
        var path = getResourcePath(filename);
        return getContent(path);
    }

    private static Path getResourcePath(String fileName) {
        try {
            var fileUrl = DocumentLoader.class.getClassLoader().getResource(fileName);
            var filePath = fileUrl.toURI();

            logger.info("Loading file: {}", filePath);
            return Paths.get(filePath);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

}
