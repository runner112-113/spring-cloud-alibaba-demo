package com.runner.config;

import org.apache.catalina.servlets.DefaultServlet;
import org.springframework.boot.loader.LaunchedURLClassLoader;
import org.springframework.boot.web.embedded.tomcat.TomcatEmbeddedWebappClassLoader;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.AbstractServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * {@link WebServerFactoryCustomizer}
 * 但是此时还是会委派给{@link LaunchedURLClassLoader} 加载,而不是由{@link TomcatEmbeddedWebappClassLoader}来加载
 *
 * @author Runner
 * @version 1.0
 * @since 2024/9/6 18:27
 */
@Configuration
public class MyConfiguration {


    @Bean
    public WebServerFactoryCustomizer webServerFactoryCustomizer() {
        return factory -> ((AbstractServletWebServerFactory)factory).addInitializers(new ServletContextInitializer() {
            @Override
            public void onStartup(ServletContext servletContext) throws ServletException {
                try {
                    Class aClass = Class.forName(DefaultServlet.class.getName());
                    servletContext.addServlet("/aaa", aClass);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }
}
