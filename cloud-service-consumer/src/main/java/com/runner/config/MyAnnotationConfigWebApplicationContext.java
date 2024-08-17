package com.runner.config;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.weaving.AspectJWeavingEnabler;
import org.springframework.context.weaving.DefaultContextLoadTimeWeaver;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class MyAnnotationConfigWebApplicationContext extends AnnotationConfigWebApplicationContext {

    @Override
    protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
        super.customizeBeanFactory(beanFactory);

        AspectJWeavingEnabler enabler = new AspectJWeavingEnabler();
        enabler.setBeanClassLoader(beanFactory.getBeanClassLoader());
        enabler.postProcessBeanFactory(beanFactory);
//        ClassLoader classLoader = beanFactory.getBeanClassLoader();
//        AspectJWeavingEnabler.enableAspectJWeaving(null, classLoader);
    }
}
