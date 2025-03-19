package com.runner.ai.rag.docreader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TikasDocumentReader implements DocumentReaderStrategy{
    @Override
    public boolean support(Resource resource) {
        if (resource.getFilename() == null) return false;
        return resource.getFilename().endsWith(".docx") || resource.getFilename().endsWith(".pptx") || resource.getFilename().endsWith(".html");
    }

    @Override
    public List<Document> read(Resource resource, Object config) {
        return new TikaDocumentReader(resource).get();
    }

    @Override
    public List<Document> read(Resource resource) {
        return new TikaDocumentReader(resource).get();
    }
}
