package com.paascloud.gateway.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
public class SecurityConfig extends ReactiveUserDetailsServiceAutoConfiguration {

    @Autowired
    private MyAuthenticationProvider myAuthenticationProvider;

    @Autowired
    private MyServerSecurityContextRepository myServerSecurityContextRepository;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
//        http.addFilterAt()
        UserDetailsRepositoryReactiveAuthenticationManager manager = new UserDetailsRepositoryReactiveAuthenticationManager(myAuthenticationProvider);
        manager.setPasswordEncoder(new MyPasswordEncoder());
        return http.authenticationManager(manager)
                .authorizeExchange()
                .pathMatchers("/actuator/**")
                .permitAll()
                .pathMatchers("/**")
                .authenticated()
                .and()
                .securityContextRepository(myServerSecurityContextRepository)
                .cors()
                .and().csrf()
                .and().formLogin()
                .and().logout()
                .and().oauth2Login()
                .and().build();
    }

}
