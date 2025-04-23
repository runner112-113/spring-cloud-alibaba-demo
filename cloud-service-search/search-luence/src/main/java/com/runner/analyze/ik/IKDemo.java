package com.runner.analyze.ik;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;

public class IKDemo {
    public static void main(String[] args) {
        String text = "美股中新能源";

        try (StringReader reader = new StringReader(text)){
            IKSegmenter ikSegmenter = new IKSegmenter(reader, true);
            Lexeme lexeme;
            while ((lexeme = ikSegmenter.next()) != null){
                System.out.println(lexeme.getLexemeText());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
