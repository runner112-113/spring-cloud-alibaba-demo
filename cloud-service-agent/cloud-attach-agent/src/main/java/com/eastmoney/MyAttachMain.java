package com.eastmoney;

import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class MyAttachMain {
    public static void main(String[] args) throws IOException, Exception {
        VirtualMachine vm = VirtualMachine.attach("20184");
        try {
            vm.loadAgent("D:\\myworkspace\\demo\\spring-cloud-alibaba-demo\\cloud-attach-agent\\target\\cloud-attach-agent-jar-with-dependencies.jar");
        }finally {
            vm.detach();
        }
    }
}
