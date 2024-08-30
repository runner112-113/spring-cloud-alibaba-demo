package com.eastmoney;

import com.eastmoney.aspectj.MyService;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        MyService.testStatic();
        System.out.println( "Hello World!" );
        MyService myService = new MyService();
        myService.test();
    }
}
