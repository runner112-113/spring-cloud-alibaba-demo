package com.runner.analyze.filter;

import com.runner.analyze.SynonymAnalyzer;
import com.runner.analyze.SynonymEngine;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeSource;

import java.io.IOException;
import java.io.StringReader;
import java.util.Stack;

public class SynonymFilter extends TokenFilter {
    private static final String TOKEN_TYPE_SYNONYM = "SYNONYM";

    private Stack<String> synonymStack;
    private SynonymEngine engine;
    private AttributeSource.State current;

    private CharTermAttribute termAtt;
    private PositionIncrementAttribute posIncrAtt;

    public SynonymFilter(TokenStream input, SynonymEngine engine) {
        super(input);
        this.synonymStack = new Stack<>();
        this.engine = engine;
        this.termAtt = addAttribute(CharTermAttribute.class);
        this.posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (synonymStack.size() > 0) {
            String syn = synonymStack.pop();
            restoreState(current);
            termAtt.setEmpty().append(syn);
            posIncrAtt.setPositionIncrement(0);
            return true;
        }

        // 输入流中获取下一个词汇单元
        if (!input.incrementToken())
            return false;

        if (addAliasesToStack()) {
            current = captureState();
        }

        return true;
    }

    private boolean addAliasesToStack() throws IOException {
        String[] synonyms = engine.getSynonyms(termAtt.toString());
        if (synonyms == null)
            return false;

        for (String synonym : synonyms) {
            synonymStack.push(synonym);
        }

        return true;
    }


    public static void main(String[] args) throws IOException {
        SynonymEngine engine = new SynonymEngine();
        Analyzer analyzer = new SynonymAnalyzer(engine);

        String text = "The quick brown fox jumps over the lazy dog.";
        TokenStream stream = analyzer.tokenStream("content", new StringReader(text));

        CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);

        stream.reset();
        while (stream.incrementToken()) {
            System.out.println(termAtt.toString());
        }
        stream.close();
    }
}
