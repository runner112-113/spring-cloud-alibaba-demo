package com.runner.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.stereotype.Component;

@Component
public class MyLoadTimeWeaverAware implements LoadTimeWeaverAware {
    @Override
    public void setLoadTimeWeaver(LoadTimeWeaver loadTimeWeaver) {

    }
}
