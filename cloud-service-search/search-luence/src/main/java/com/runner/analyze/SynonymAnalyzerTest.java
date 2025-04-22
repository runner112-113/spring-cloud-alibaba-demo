package com.runner.analyze;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.RAMDirectory;

public class SynonymAnalyzerTest {

    private IndexSearcher searcher;
    private Analyzer synonymAnalyzer = new SynonymAnalyzer(new SynonymEngine());

    public void setUp() throws Exception {
        RAMDirectory directory = new RAMDirectory();
        IndexWriterConfig iwc = new IndexWriterConfig(synonymAnalyzer);
        IndexWriter writer = new IndexWriter(directory, iwc);

        Document doc = new Document();
        doc.add(new TextField("content", "The quick brown fox jumps over the lazy dog", Field.Store.YES));
        writer.addDocument(doc);

        writer.close();
        DirectoryReader reader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(reader);
    }

    public void tearDown() throws Exception {
        searcher.getIndexReader().close();
    }

    public void testSearchByAPI() throws Exception {
        // 搜索 "hops"
        Query tq = new TermQuery(new Term("content", "hops"));
        System.out.println(1 == searcher.search(tq, 10).totalHits.value);

        // 搜索 "fox hops"
        PhraseQuery pq = new PhraseQuery.Builder()
                .add(new Term("content", "fox"))
                .add(new Term("content", "hops"))
                .build();
        System.out.println(1 ==  searcher.search(pq, 10).totalHits.value);
    }
}
