package com.runner.entity;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * TODO
 *
 * @author Runner
 * @version 1.0
 * @since 2024/8/31 14:35
 */
public class MyBean implements SmartInitializingSingleton {


    @Autowired
    ApplicationContext applicationContext;

    int age;


    @Override
    public void afterSingletonsInstantiated() {
        age =3;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public int getAge() {
        return age;
    }
}
