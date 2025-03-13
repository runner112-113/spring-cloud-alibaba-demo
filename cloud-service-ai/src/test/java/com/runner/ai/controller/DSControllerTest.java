package com.runner.ai.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.*;

@SpringBootTest
public class DSControllerTest {

    @Autowired
    DSController dsController;

    @Test
    public void generate() {
        System.out.println(dsController.generate("你好,你是谁"));
    }
}