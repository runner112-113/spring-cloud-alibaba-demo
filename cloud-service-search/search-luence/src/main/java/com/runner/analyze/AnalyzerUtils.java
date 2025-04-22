package com.runner.analyze;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.tokenattributes.*;

import java.io.IOException;
import java.io.StringReader;

public class AnalyzerUtils {

    public static void displayTokens(Analyzer analyzer,
                                     String text) throws IOException {
        displayTokens(analyzer.tokenStream("contents",
                new StringReader(text)));
    }

    public static void displayTokens(TokenStream stream)
            throws IOException {
        CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
        stream.reset();
        while(stream.incrementToken()) {
            System.out.print("[" + term.toString() + "]");
//            System.out.println(new String(term.buffer()));
        }
        stream.close();
    }


    public static void displayTokensWithFullDetails(Analyzer analyzer,
                                                    String text)
            throws IOException {
        TokenStream stream = analyzer.tokenStream("contents", new StringReader(text)); // 执行分析

        CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
        PositionIncrementAttribute posIncr = stream.addAttribute(PositionIncrementAttribute.class); // 获取有用属性
        OffsetAttribute offset = stream.addAttribute(OffsetAttribute.class);
        TypeAttribute type = stream.addAttribute(TypeAttribute.class);

        int position = 0;
        stream.reset();
        while (stream.incrementToken()) { // 递归处理所有语汇单元
            int increment = posIncr.getPositionIncrement();
            if (increment > 0) {
                position = position + increment;
                System.out.println(); // 计算位置信息并打印
                System.out.print(position + ": ");
            }

            System.out.print("[" + term.toString() + ":" + // 打印所有语汇单元细节信息
                    offset.startOffset() + "->" +
                    offset.endOffset() + ":" +
                    type.type() + "]");
        }
        stream.close();
        System.out.println();

    }

    public static void main(String[] args) throws IOException {
//        displayTokensWithFullDetails(new SimpleAnalyzer(), "The quick brown fox jumped over the lazy dog");
        MetaphoneReplacementAnalyzer analyzer = new MetaphoneReplacementAnalyzer();
        AnalyzerUtils.displayTokens(analyzer, "The quick brown fox jumped over the lazy dog");
        System.out.println("");
        AnalyzerUtils.displayTokens(analyzer, "Tha quik brown phox jumpd ovvar tha lazi dag");

    }
}
