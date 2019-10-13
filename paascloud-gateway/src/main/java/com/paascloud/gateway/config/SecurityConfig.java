package com.paascloud.gateway.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.reactive.function.client.WebClient;

@EnableWebFluxSecurity
public class SecurityConfig extends ReactiveUserDetailsServiceAutoConfiguration {

    @Autowired
    private MyAuthenticationProvider myAuthenticationProvider;

    @Autowired
    private MyServerSecurityContextRepository myServerSecurityContextRepository;

    @Autowired
    private CorsWebFilter corsFilter;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        UserDetailsRepositoryReactiveAuthenticationManager manager = new UserDetailsRepositoryReactiveAuthenticationManager(myAuthenticationProvider);
        manager.setPasswordEncoder(new MyPasswordEncoder());
        return http.authenticationManager(manager)
                .authorizeExchange()
                .pathMatchers("/actuator/**","/registered/**","/uac/seata/rollback/**")
                .permitAll()
                .pathMatchers("/**")
                .authenticated()
                .and()
                .securityContextRepository(myServerSecurityContextRepository)
                .cors().disable()
                .addFilterAt(corsFilter, SecurityWebFiltersOrder.CORS)
                .csrf().disable()
                .formLogin()
                .loginPage("http://47.104.150.14:80").requiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/login"))
                .authenticationSuccessHandler(new MyServerAuthenticationSuccessHandler())
                .authenticationFailureHandler(new MyServerAuthenticationFailureHandler())
                .and().logout()
//               .and().oauth2Login()
                .and().build();
    }

}
