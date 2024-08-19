package com.eastmoney;

import com.runner.interfaces.MyAgentIntercept;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * TODO
 *
 * @author Runner
 * @version 1.0
 * @since 2024/8/19 10:35
 */
public class CloudAgent {

    public static void premain(String arguments, Instrumentation instrumentation) {
        new AgentBuilder.Default()
                .type(isAnnotatedWith(MyAgentIntercept.class))
                .transform(new AgentBuilder.Transformer() {
                    public DynamicType.Builder transform(DynamicType.Builder builder,
                                                         TypeDescription typeDescription,
                                                         ClassLoader classloader,
                                                         JavaModule module) {
                        return builder.method(named("sayHello"))
                                .intercept(FixedValue.value("new hello world"));
                    }
                }).installOn(instrumentation);
    }
}
