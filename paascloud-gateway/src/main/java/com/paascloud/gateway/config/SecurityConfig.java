package com.paascloud.gateway.config;


import com.paascloud.gateway.config.oauth2.Oauth2AuthoConfig;
import com.paascloud.provider.service.SysUserFeignApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.reactive.CorsWebFilter;

@EnableWebFluxSecurity
public class SecurityConfig extends ReactiveUserDetailsServiceAutoConfiguration {

    @Autowired
    private MyServerSecurityContextRepository myServerSecurityContextRepository;

    @Autowired
    private CorsWebFilter corsFilter;

    @Autowired
    private SysUserFeignApi userFeignApi;

    @Autowired
    private Oauth2AuthoConfig  oauth2AuthoConfig;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        UserDetailsRepositoryReactiveAuthenticationManager manager = new UserDetailsRepositoryReactiveAuthenticationManager(new MyAuthenticationProvider(userFeignApi));
        manager.setPasswordEncoder(new MyPasswordEncoder());
        oauth2AuthoConfig.configure(http);
        return http
                .authenticationManager(manager)
                .authorizeExchange()
                .pathMatchers("/actuator/**","/uac/registered/**")
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
                .authenticationSuccessHandler(new MyServerAuthenticationSuccessHandler(userFeignApi))
                .authenticationFailureHandler(new MyServerAuthenticationFailureHandler())
                .and().logout()
//                .and().addFilterAt(codeGrantWebFilter, SecurityWebFiltersOrder.OAUTH2_AUTHORIZATION_CODE)
//                .addFilterAt(oauthRedirectFilter, SecurityWebFiltersOrder.HTTP_BASIC)
//                .and().oauth2Client()
//               .and().oauth2Login()
                .and().build();
    }

}
