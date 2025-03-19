package com.runner.ai.rag.docreader;

import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;

import java.nio.file.Path;
import java.util.List;

public interface DocumentReaderStrategy {

    boolean support(Resource resource);

    // TODO custom Config ,Adapter?
    List<Document> read(Resource resource,Object config);

    List<Document> read(Resource resource);

}
