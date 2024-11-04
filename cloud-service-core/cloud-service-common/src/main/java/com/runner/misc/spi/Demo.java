package com.runner.misc.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * TODO
 *
 * @author Runner
 * @version 1.0
 * @since 2024/11/4 10:17
 */
public class Demo {
    public static void main(String[] args) {
        ServiceLoader<Object> load = ServiceLoader.load(Object.class);
        Iterator<Object> iterator = load.iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();

        }
    }
}
