package com.eastmoney.agent;

import com.runner.interfaces.MyAgentIntercept;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * TODO
 *
 * @author Runner
 * @version 1.0
 * @since 2024/8/19 14:01
 */
public class CloudAttachAgent {

    public static void agentmain(String arguments, Instrumentation instrumentation) throws UnmodifiableClassException {
        System.out.println("agentmain called");

        new AgentBuilder.Default()
                .type(isAnnotatedWith(MyAgentIntercept.class))
                .transform(new AgentBuilder.Transformer() {
                    public DynamicType.Builder transform(DynamicType.Builder builder,
                                                         TypeDescription typeDescription,
                                                         ClassLoader classloader,
                                                         JavaModule module) {
                        System.out.println("transform transform transform ...");
                        return builder.method(named("sayHello"))
                                .intercept(FixedValue.value("new hello world"));
                    }
                }).installOn(instrumentation);



        // 重新transform class
        Class classes[] = instrumentation.getAllLoadedClasses();
        for (int i = 0; i < classes.length; i++) {
            System.out.println(classes[i].getName());
            if (classes[i].getName().equals("com.runner.controller.dubbo.DubboDemoController")) {
                System.out.println("Reloading: " + classes[i].getName());
                instrumentation.retransformClasses(classes[i]);
                break;
            }
        }
    }
}
