package com.runner.ai.vector.handle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisVectorDaoTest {

    @Autowired
    RedisVectorDao redisVectorDao;

    @Test
    public void embed() {
        redisVectorDao.embed();
        System.out.println("success ...");
    }
}