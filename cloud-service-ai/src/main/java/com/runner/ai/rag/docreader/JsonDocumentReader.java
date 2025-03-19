package com.runner.ai.rag.docreader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JsonDocumentReader implements DocumentReaderStrategy{
    @Override
    public boolean support(Resource resource) {
        if (resource.getFilename() == null) return false;
        return resource.getFilename().endsWith(".json");
    }

    @Override
    public List<Document> read(Resource resource, Object config) {
        return new JsonReader(resource).get();
    }

    @Override
    public List<Document> read(Resource resource) {
        return new JsonReader(resource).get();
    }
}
