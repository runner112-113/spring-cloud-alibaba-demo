package com.runner;

import com.alibaba.cloud.nacos.registry.NacosServiceRegistryAutoConfiguration;
import com.runner.datasource.mysql.mapper.CityMapper;
import com.runner.datasource.redis.annotations.EnableRedisMetrics;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Hello world!
 *
 */
@SpringBootApplication(exclude = NacosServiceRegistryAutoConfiguration.class)
@EnableDubbo
//@EnableDiscoveryClient
//@EnableLoadTimeWeaving
@EnableWebSecurity
@EnableMethodSecurity
@EnableRedisMetrics
//@ComponentScan(basePackageClasses = ConsumerApplication.class)
public class ConsumerApplication implements CommandLineRunner, BeanFactoryAware {



/*    @Bean
    public AspectJWeavingEnabler enabler() {
        return new AspectJWeavingEnabler();
    }*/
    public static void main( String[] args ) {

/*        SpringApplication springApplication = new SpringApplication();
        springApplication.*/

        ConfigurableApplicationContext context = SpringApplication.run(ConsumerApplication.class, args);
/*        ObjectPostProcessor objectPostProcessor = context.getBean(ObjectPostProcessor.class);

        Map<String, DataSource> beansOfType = context.getBeansOfType(DataSource.class);

        MyBean myBean = new MyBean();
        objectPostProcessor.postProcess(myBean);

        System.out.println(myBean.getAge());*/





//        AnnotationConfigWebApplicationContext context = new MyAnnotationConfigWebApplicationContext();
//        context.addBeanFactoryPostProcessor(aspectJWeavingEnabler);
//        context.register(ConsumerApplication.class);
//        ConfigurableApplicationContext context = SpringApplication.run(new Class[]{AspectJWeavingEnabler.class,ConsumerApplication.class }, args);
//        context.refresh();
/*        DubboDemoController controller = context.getBean(DubboDemoController.class);
//        DubboDemoController controller = new DubboDemoController();
        controller.sayHello();


        DubboDemoController dubboDemoController = new DubboDemoController();
        dubboDemoController.sayHello();*/
    }


    private BeanFactory beanFactory;
    private final CityMapper cityMapper;

    public ConsumerApplication(CityMapper cityMapper) {
        this.cityMapper = cityMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        beanFactory.getBean("cityMapper");
        beanFactory.getBean(BeanFactory.FACTORY_BEAN_PREFIX + "cityMapper");
        RedisTemplate redisTemplate = beanFactory.getBean("redisTemplate", RedisTemplate.class);
        redisTemplate.opsForValue().set("test", 1);
        System.out.println(this.cityMapper.findByState("CA"));
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
