package com.runner.ai.rag.docreader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

@Component
public class PdfDocumentReader implements DocumentReaderStrategy{
    @Override
    public boolean support(Resource resource) {
        if (resource.getFilename() == null) return false;
        return resource.getFilename().endsWith(".pdf");
    }

    @Override
    public List<Document> read(Resource resource,Object config) {
        Assert.isAssignable(PdfDocumentReaderConfig.class, config.getClass(), "config must PdfDocumentReaderConfig subClass");
        return new PagePdfDocumentReader(resource, (PdfDocumentReaderConfig) config).read();
    }

    @Override
    public List<Document> read(Resource resource) {
        return new PagePdfDocumentReader(resource).read();
    }
}
