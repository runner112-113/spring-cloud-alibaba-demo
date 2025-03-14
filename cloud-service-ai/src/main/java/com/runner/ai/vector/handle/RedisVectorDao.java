package com.runner.ai.vector.handle;
import com.runner.ai.vector.document.reader.MyTextReader;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RedisVectorDao {

    @Autowired
    MyTextReader textReader;

    @Autowired
    RedisVectorStore redisVectorStore;

    public void embed() {
        List<Document> splitedDocs = new TokenTextSplitter().apply(textReader.loadText());
        redisVectorStore.add(splitedDocs);
    }
}
