package com.runner.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.config.annotation.web.AbstractRequestMatcherRegistry;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

import javax.servlet.DispatcherType;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {


    private static final String AUTHORITY_PREFIX = "EM_ROLE_";



/*    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.requestMatcher(EndpointRequest.toAnyEndpoint());
        http.authorizeRequests((requests) -> requests.anyRequest().hasRole("ENDPOINT_ADMIN"));
        http.httpBasic(withDefaults());
        return http.build();
    }*/

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // 匹配器
        return httpSecurity.requestMatchers(AbstractRequestMatcherRegistry::anyRequest)
                .anonymous(anonymousCustomizer -> anonymousCustomizer.authorities(generateRole("TEST")))
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry
                                // endpoint数据
//                                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ENDPOINT_ADMIN")
                                .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
                                .mvcMatchers("/aa").permitAll()
                                .requestMatchers(request -> request.isUserInRole("wee")).access(AuthenticatedAuthorizationManager.authenticated())
                                .requestMatchers(new RegexRequestMatcher("/resource/[A-Za-z0-9]+", "POST")).hasAuthority("USER")
                                .regexMatchers("/resource/[A-Za-z0-9]+").hasAnyAuthority("USER")
                                .antMatchers(HttpMethod.GET).permitAll()
                                .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                                .anyRequest().hasAuthority(generateRole("TEST"))
                )
                .build();

    }

    @Bean
    static GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("EM_ROLE_");
    }

    private static String generateRole(String roleCode) {
        return AUTHORITY_PREFIX + roleCode;
    }



/*    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.db0")
    public DataSource dataSource0() {
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
//        return new HikariDataSource();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.db1")
    public DataSource dataSource1() {
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
//        return new HikariDataSource();
    }*/
}
