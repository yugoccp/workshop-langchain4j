package org.acme.utils;

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

    public static String getResource(String filename) {
        var documentPath = toResourcePath(filename);
        var fileExt = getExtension(filename);

        if (fileExt.equals("pdf")) {
            return FileSystemDocumentLoader.loadDocument(documentPath, new ApachePdfBoxDocumentParser()).text();
        } else {
            return FileSystemDocumentLoader.loadDocument(documentPath).text();
        }
    }

    private static Path toResourcePath(String fileName) {
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
