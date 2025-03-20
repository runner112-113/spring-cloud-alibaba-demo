package com.runner.aop;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import com.alibaba.fastjson.JSONObject;
import com.eastmoney.content.process.base.api.response.ResponseBase;
import com.eastmoney.content.process.base.api.response.ResponseCom;
import org.apache.logging.log4j.LogManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Aspect
@Component
public class SentinelAop {
    private static final Logger log = LoggerFactory.getLogger(SentinelAop.class);

    /**
     * 降级熔断开关
     */
    private final Boolean sentinelFlag;

    /**
     * 熔断返回话术
     */
    private final String degradeSentence;

    /**
     * 状态成功状态码
     */
    private final List<String> successCodes;

    private final Integer slowTime;

    public SentinelAop(@Value("${sentinel.flag}") Boolean sentinelFlag,
                       @Value("${degrade.sentence}") String degradeSentence,
                       @Value("${success.code}") List<String> successCodes,
                       @Value("${sentinel.slowtime}") Integer slowTime) {
        this.sentinelFlag = sentinelFlag;
        this.degradeSentence = degradeSentence;
        this.successCodes = successCodes;
        this.slowTime = slowTime;
    }

    @Pointcut("execution(* com.content_process.np.smart.tag.controller.pick.controller..*.*(..))")
    public void logService() {
    }

    /**
     * 接口全局降级熔断器
     * @param jp
     * @return
     * @throws Throwable
     */
    @Around("logService()")
    public Object around(ProceedingJoinPoint jp) throws Throwable {
        long start = System.currentTimeMillis();
        String key = createKey(jp);
        Object result = null;
        Entry entry1 = null;
        Entry entry2 = null;
        try {
            //是否开启降级熔断
            if (sentinelFlag) {
                entry1 = SphU.entry(key + "_slow");
                entry2 = SphU.entry(key + "_error");
            }
            result = jp.proceed(jp.getArgs());
            if (sentinelFlag && result instanceof ResponseBase){
                ResponseBase<?> responseBase = (ResponseBase<?>) result;
                String code = responseBase.getCode();
                if (resultError(code,responseBase.getMsg())){
                    throw new Exception("结果异常，code="+code);
                }
            }
        }catch (BlockException e) {
            log.error("响应已熔断"+key,e);
            recordMetric(jp,"smart_tag_pick_controller_block",start);
            result=ResponseCom.Fail(degradeSentence);
        } catch (Exception e) {
            log.error("响应出现异常:{},result:{}",key,JSONObject.toJSONString(result),e);
            recordMetric(jp,"smart_tag_pick_controller_error",start);
            if (sentinelFlag) {
                Tracer.traceEntry(e, entry2);
            }
        }finally {
            if (entry2 != null) {
                entry2.exit();
            }
            if (entry1 != null) {
                entry1.exit();
            }
            recordMetric(jp,"smart_tag_pick_controller",start);
        }
        return result;
    }



    @PostConstruct
    private void controllerInit(){
        // 基础包名，替换为你的项目的基础包名
        String basePackage = "com.content_process.np.smart.tag.controller.pick.controller";
        List<DegradeRule> degradeRules = new ArrayList<>();

        /**
         * <dependency>
         *     <groupId>org.reflections</groupId>
         *     <artifactId>reflections</artifactId>
         *     <version>0.10.2</version>
         * </dependency>
         */
        Set<Class<?>> classes = new Reflections(basePackage).getTypesAnnotatedWith(RequestMapping.class);
        for (Class<?> clazz : classes) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
                if (declaredAnnotations.length > 0 ) {
                    String name = clazz.getSimpleName() + "_" + method.getName();
                    DegradeRule rule1 = new DegradeRule(name+"_slow")
                            .setGrade(CircuitBreakerStrategy.SLOW_REQUEST_RATIO.getType())
                            //慢调用阈值单位毫秒
                            .setCount(slowTime)
                            //熔断时长
                            .setTimeWindow(60*5)
                            //熔断触发的最小请求数
                            .setMinRequestAmount(10)
                            //统计单位
                            .setStatIntervalMs(30*1000)
                            //慢调用比例阈值
                            .setSlowRatioThreshold(0.6);
                    degradeRules.add(rule1);

                    DegradeRule rule2 = new DegradeRule(name+"_error")
                            .setGrade(CircuitBreakerStrategy.ERROR_RATIO.getType())
                            .setStatIntervalMs(30*1000)
                            .setMinRequestAmount(10)
                            .setCount(1)
                            .setTimeWindow(5*60);


                    degradeRules.add(rule2);
                }
            }
        }
        DegradeRuleManager.loadRules(degradeRules);
        log.info("熔断器加载成功="+degradeRules);
    }

    private boolean resultError(String code, String msg){
        return !successCodes.contains(code) || ("999".equalsIgnoreCase(code) && "Fail".equalsIgnoreCase(msg));
    }

    private void recordMetric(ProceedingJoinPoint jp, String metricName, long startTime){
        String key = createKey(jp);
        HashMap<String, String> hashMap = new HashMap<>(1);
        hashMap.put("method",key);
        long cost = System.currentTimeMillis() - startTime;
        logger.metric(metricName,cost,hashMap);
    }

    private String createKey(ProceedingJoinPoint jp){
        String simpleName = jp.getTarget().getClass().getSimpleName();
        String methodName = jp.getSignature().getName();
        return simpleName + "_" + methodName;
    }



}
