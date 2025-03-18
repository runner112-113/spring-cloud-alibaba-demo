package com.runner.ai.vector.handle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
//@TestPropertySource(value = "classpath:application-test.yml")
public class RedisVectorDaoTest {

    @Autowired
    RedisVectorDao redisVectorDao;

    @Test
    public void embed() {
        redisVectorDao.embed();
        System.out.println("success ...");
    }


    @Test
    public void similaritySearch() {
        List<Document> documents = redisVectorDao.similaritySearch("公园");
        List<String> texts = documents.stream().map(Document::getText).collect(Collectors.toList());
    }
}