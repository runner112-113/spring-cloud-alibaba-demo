package com.runner.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;

public class Indexer {

    private IndexWriter writer;

    public Indexer(String indexDir) throws IOException {
        FSDirectory dir = FSDirectory.open(new File(indexDir).toPath());
        IndexWriterConfig config = new IndexWriterConfig();
        writer = new IndexWriter(dir, config);
    }

    public void close() throws IOException {
        writer.close();
    }

    public  int index(String dataDir, FileFilter filter) throws IOException {
        File[] files = new File(dataDir).listFiles();
        for (File file : files) {
            if (!file.isDirectory() &&
                    !file.isHidden() &&
                    file.exists() &&
                    (filter == null || filter.accept(file))) {
                indexFile(file);
            }
        }

        return writer.numRamDocs();
    }

    protected Document getDoc(File file) throws IOException {
        Document document = new Document();
        document.add(new Field("contents", new FileReader(file),new FieldType()));
        FieldType fieldType = new FieldType();
        fieldType.setStored(true);
        fieldType.setIndexOptions(IndexOptions.DOCS);
        document.add(new Field("filename", file.getName(),fieldType));
        FieldType fieldPathType = new FieldType();
        // 存储原始值
        fieldPathType.setStored(true);
        // 禁止分词
        fieldPathType.setTokenized(false);
        document.add(new Field("fullpath",file.getCanonicalPath(), fieldType));
        return document;
    }

    private void indexFile(File file) throws IOException {
        System.out.println("Indexing " + file.getCanonicalPath());
        Document doc = getDoc(file);
        writer.addDocument(doc);

    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            throw new RuntimeException();
        }
        String indexDir = args[0];
        String dataDir = args[1];

        long start = System.currentTimeMillis();
        Indexer indexer = new Indexer(indexDir);
        int numIndexed;
        try {
            indexer.index(dataDir, new TextFilesFilter());
        }finally {
            indexer.close();
        }
        long end = System.currentTimeMillis();
    }

    private static class TextFilesFilter implements FileFilter {
        @Override
        public boolean accept(File path) {
            return path.getName().toLowerCase().endsWith(".txt");
        }
    }
}
