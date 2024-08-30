package com.runner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.AbstractRequestMatcherRegistry;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {


    private static final String AUTHORITY_PREFIX = "EM_ROLE_";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // 匹配器
        return httpSecurity.requestMatchers(AbstractRequestMatcherRegistry::anyRequest)
                .anonymous(anonymousCustomizer -> anonymousCustomizer.authorities(generateRole("TEST")))
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry.anyRequest().hasAuthority(generateRole("TEST")))
                .build();

    }

    @Bean
    static GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("EM_ROLE_");
    }

    private static String generateRole(String roleCode) {
        return AUTHORITY_PREFIX + roleCode;
    }
}
