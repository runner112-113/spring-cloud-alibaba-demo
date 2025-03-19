package com.runner.ai.rag.docreader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TextDocumentReader implements DocumentReaderStrategy{
    @Override
    public boolean support(Resource resource) {
        if (resource.getFilename() == null) return false;
        return resource.getFilename().endsWith(".txt");
    }

    @Override
    public List<Document> read(Resource resource, Object config) {
        return new TextReader(resource).get();
    }

    @Override
    public List<Document> read(Resource resource) {
        return new TextReader(resource).get();
    }
}
