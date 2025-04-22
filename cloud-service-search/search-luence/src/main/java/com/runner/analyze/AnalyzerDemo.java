package com.runner.analyze;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

public class AnalyzerDemo {
    private static final String[] examples = {
                "The quick brown fox jumped over the lazy dog",
                "XY&Z Corporation - xyz@example.com"
        };

    private static final Analyzer[] analyzers = new Analyzer[] {
            new WhitespaceAnalyzer(),
            new SimpleAnalyzer(),
            new StopAnalyzer(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET),
            new StandardAnalyzer()
    };

    public static void main(String[] args) throws IOException {
        String[] strings = examples;
        if (args.length > 0) {
            strings = args; // 分析处理的命令行参数
        }
        for (String text : strings) {
            analyze(text);
        }
    }

    private static void analyze(String text) throws IOException {
        System.out.println("Analyzing \"" + text + "\"");
        for (Analyzer analyzer : analyzers) {
            String name = analyzer.getClass().getSimpleName();
            System.out.println("  " + name + ":");
            System.out.print("    ");
            AnalyzerUtils.displayTokens(analyzer, text); // 执行实际操作
            System.out.println("\n");
        }
    }


/*    public void testKoolKat() throws Exception {
        RAMDirectory directory = new RAMDirectory();
        Analyzer analyzer = new MetaphoneReplacementAnalyzer();
        IndexWriter writer = new IndexWriter(directory, analyzer, true,
                IndexWriter.MaxFieldLength.UNLIMITED);

        Document doc = new Document();
        doc.add(new Field("contents",
                "cool cat",
                Field.Store.YES,
                Field.Index.ANALYZED));
        writer.addDocument(doc);
        writer.close();

        IndexSearcher searcher = new IndexSearcher(directory);
        Query query = new QueryParser(Version.LUCENE_30,
                "contents", analyzer)
                .parse("kool kat");

        TopDocs hits = searcher.search(query, 1);
        assertEquals(1, hits.totalHits); // 核实匹配
        int docID = hits.scoreDocs[0].doc;
        doc = searcher.doc(docID);
        assertEquals("cool cat", doc.get("contents")); // 检索初始值
        searcher.close();
    }*/



    public void testKoolKat() throws Exception {
        RAMDirectory directory = new RAMDirectory();

        // 假设 MetaphoneReplacementAnalyzer 是你自定义的分析器
        Analyzer analyzer = new MetaphoneReplacementAnalyzer();

        // 使用 IndexWriterConfig 配置 IndexWriter
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(directory, config);

        Document doc = new Document();
        // 使用 TextField 而不是旧版本的 Field 类型
        doc.add(new TextField("contents", "cool cat", Field.Store.YES));
        writer.addDocument(doc);
        writer.close();

        // 使用 DirectoryReader 来打开索引目录
        DirectoryReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        // 创建 QueryParser 并解析查询
        QueryParser parser = new QueryParser("contents", analyzer);
        Query query = parser.parse("kool kat");

        // 执行搜索
        TopDocs hits = searcher.search(query, 1);
        if (hits.totalHits.value == 1) { // 核实匹配
            ScoreDoc scoreDoc = hits.scoreDocs[0];
            int docID = scoreDoc.doc;
            doc = searcher.doc(docID);
            // 检索初始值
            if (!doc.get("contents").equals("cool cat")) {
                throw new IllegalStateException("检索内容不匹配");
            }
        } else {
            throw new IllegalStateException("预期找到一个文档，但结果不同");
        }

        reader.close(); // 关闭 DirectoryReader
    }
}
