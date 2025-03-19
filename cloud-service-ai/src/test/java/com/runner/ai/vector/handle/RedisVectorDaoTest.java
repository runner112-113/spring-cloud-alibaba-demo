package com.runner.ai.vector.handle;

import com.runner.ai.rag.docreader.DocumentReaderStrategy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
//@TestPropertySource(value = "classpath:application-test.yml")
public class RedisVectorDaoTest {

    @Autowired
    RedisVectorDao redisVectorDao;

    @Autowired
    ResourcePatternResolver resolver;

    @Autowired
    List<DocumentReaderStrategy> readerStrategies;

    @Test
    public void embed() {
        redisVectorDao.embed();
        System.out.println("success ...");
    }


    @Test
    public void similaritySearch() {
        List<Document> documents = redisVectorDao.similaritySearch("易速鲜花");
        List<String> texts = documents.stream().map(Document::getText).collect(Collectors.toList());
        for (String text : texts) {
            System.out.println(text);
        }
    }


    @Test
    public void vectorization() throws IOException {
        Resource[] resources = resolver.getResources("classpath:files/**");
        for (Resource resource : resources) {
            for (DocumentReaderStrategy readerStrategy : readerStrategies) {
                if (readerStrategy.support(resource)) {
                    List<Document> documents = readerStrategy.read(resource);
                    List<Document> splitedDocs = new TokenTextSplitter().apply(documents);
                    redisVectorDao.redisVectorStore.add(splitedDocs);
                    break;               }
            }
            System.out.println(resource.getFilename() + "reader fail");
        }
    }
}