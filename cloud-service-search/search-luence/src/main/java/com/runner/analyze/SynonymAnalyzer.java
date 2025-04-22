package com.runner.analyze;

import com.runner.analyze.filter.SynonymFilter;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class SynonymAnalyzer extends StopwordAnalyzerBase {

    private final SynonymEngine engine;

    public SynonymAnalyzer(SynonymEngine engine) {
        this(engine, CharArraySet.EMPTY_SET);
    }

    public SynonymAnalyzer(SynonymEngine engine, CharArraySet stopwords) {
        super(stopwords);
        this.engine = engine;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        StandardTokenizer src = new StandardTokenizer();
        TokenStream tokenStream = new LowerCaseFilter(src);
        tokenStream = new StopFilter(tokenStream, stopwords);
        tokenStream = new SynonymFilter(tokenStream, engine);
        return new TokenStreamComponents(src, tokenStream);
    }
}
