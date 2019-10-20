package com.paascloud.gateway.config.oauth2;

import com.paascloud.gateway.config.MyServerAuthenticationFailureHandler;
import com.paascloud.gateway.config.MyServerAuthenticationSuccessHandler;
import com.paascloud.gateway.config.MyServerSecurityContextRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.DelegatingReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeReactiveAuthenticationManager;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.oidc.authentication.OidcAuthorizationCodeReactiveAuthenticationManager;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.web.server.*;
import org.springframework.security.oauth2.client.web.server.authentication.OAuth2LoginAuthenticationWebFilter;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.authentication.*;
import org.springframework.security.web.server.util.matcher.MediaTypeServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class Oauth2AuthoConfig {

    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    private ServerOAuth2AuthorizedClientRepository authorizedClientRepository;

    private ReactiveAuthenticationManager authenticationManager;

    private ServerAuthenticationConverter authenticationConverter;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private MyServerSecurityContextRepository myServerSecurityContextRepository;

    /**
     * Configures the {@link ReactiveAuthenticationManager} to use. The default is
     * {@link OAuth2AuthorizationCodeReactiveAuthenticationManager}
     * @param authenticationManager the manager to use
     * @return the {@link ServerHttpSecurity.OAuth2LoginSpec} to customize
     */
    public Oauth2AuthoConfig authenticationManager(ReactiveAuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        return this;
    }

    /**
     * Gets the {@link ReactiveAuthenticationManager} to use. First tries an explicitly configured manager, and
     * defaults to {@link OAuth2AuthorizationCodeReactiveAuthenticationManager}
     *
     * @return the {@link ReactiveAuthenticationManager} to use
     */
    private ReactiveAuthenticationManager getAuthenticationManager() {
        if (this.authenticationManager == null) {
            this.authenticationManager = createDefault();
        }
        return this.authenticationManager;
    }

    private ReactiveAuthenticationManager createDefault() {
        WebClientReactiveAuthorizationCodeTokenResponseClient client = new WebClientReactiveAuthorizationCodeTokenResponseClient();
        ReactiveAuthenticationManager result = new MyOAuth2LoginReactiveAuthenticationManager(client, getOauth2UserService());

        boolean oidcAuthenticationProviderEnabled = ClassUtils.isPresent(
                "org.springframework.security.oauth2.jwt.JwtDecoder", this.getClass().getClassLoader());
        if (oidcAuthenticationProviderEnabled) {
            OidcAuthorizationCodeReactiveAuthenticationManager oidc = new OidcAuthorizationCodeReactiveAuthenticationManager(client, getOidcUserService());
            result = new DelegatingReactiveAuthenticationManager(oidc, result);
        }
        return result;
    }

    /**
     * Sets the converter to use
     * @param authenticationConverter the converter to use
     * @return the {@link ServerHttpSecurity.OAuth2LoginSpec} to customize
     */
    public Oauth2AuthoConfig authenticationConverter(ServerAuthenticationConverter authenticationConverter) {
        this.authenticationConverter = authenticationConverter;
        return this;
    }

    private ServerAuthenticationConverter getAuthenticationConverter(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        if (this.authenticationConverter == null) {
            ServerOAuth2AuthorizationCodeAuthenticationTokenConverter serverOAuth2AuthorizationCodeAuthenticationTokenConverter = new ServerOAuth2AuthorizationCodeAuthenticationTokenConverter(clientRegistrationRepository);
            serverOAuth2AuthorizationCodeAuthenticationTokenConverter.setAuthorizationRequestRepository(new MyServerAuthorizationRequestRepository(clientRegistrationRepository));
            this.authenticationConverter = serverOAuth2AuthorizationCodeAuthenticationTokenConverter;
        }
        return this.authenticationConverter;
    }

    public Oauth2AuthoConfig clientRegistrationRepository(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        return this;
    }

    public Oauth2AuthoConfig authorizedClientService(ReactiveOAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientRepository = new AuthenticatedPrincipalServerOAuth2AuthorizedClientRepository(authorizedClientService);
        return this;
    }

    public Oauth2AuthoConfig authorizedClientRepository(ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {
        this.authorizedClientRepository = authorizedClientRepository;
        return this;
    }

    /**
     * Allows method chaining to continue configuring the {@link ServerHttpSecurity}
     * @return the {@link ServerHttpSecurity} to continue configuring
     */

    public void configure(ServerHttpSecurity http) {
        ReactiveClientRegistrationRepository clientRegistrationRepository = getClientRegistrationRepository();
        ServerOAuth2AuthorizedClientRepository authorizedClientRepository = getAuthorizedClientRepository();
        OAuth2AuthorizationRequestRedirectWebFilter oauthRedirectFilter = new OAuth2AuthorizationRequestRedirectWebFilter(clientRegistrationRepository);

        ReactiveAuthenticationManager manager = getAuthenticationManager();

        AuthenticationWebFilter authenticationFilter = new OAuth2LoginAuthenticationWebFilter(manager, authorizedClientRepository);
        authenticationFilter.setRequiresAuthenticationMatcher(createAttemptAuthenticationRequestMatcher());
        authenticationFilter.setServerAuthenticationConverter(getAuthenticationConverter(clientRegistrationRepository));
        RedirectServerAuthenticationSuccessHandler redirectHandler = new RedirectServerAuthenticationSuccessHandler();

        authenticationFilter.setAuthenticationSuccessHandler(new MyServerAuthenticationSuccessHandler());
        authenticationFilter.setAuthenticationFailureHandler(new MyServerAuthenticationFailureHandler());
//        authenticationFilter.setAuthenticationFailureHandler(new ServerAuthenticationFailureHandler() {
//            @Override
//            public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange,
//                                                      AuthenticationException exception) {
//                return Mono.error(exception);
//            }
//        });
        authenticationFilter.setSecurityContextRepository(myServerSecurityContextRepository);
//        authenticationFilter.setSecurityContextRepository(new WebSessionServerSecurityContextRepository());

        MediaTypeServerWebExchangeMatcher htmlMatcher = new MediaTypeServerWebExchangeMatcher(
                MediaType.TEXT_HTML);
        htmlMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));
//        Map<String, String> urlToText = http.oauth2Login.getLinks();
//        if (urlToText.size() == 1) {
//            http.defaultEntryPoints.add(new DelegatingServerAuthenticationEntryPoint.DelegateEntry(htmlMatcher, new RedirectServerAuthenticationEntryPoint(urlToText.keySet().iterator().next())));
//        } else {
//            http.defaultEntryPoints.add(new DelegatingServerAuthenticationEntryPoint.DelegateEntry(htmlMatcher, new RedirectServerAuthenticationEntryPoint("/login")));
//        }

        http.addFilterAt(oauthRedirectFilter, SecurityWebFiltersOrder.HTTP_BASIC);
        http.addFilterAt(authenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION);
    }

    private ServerWebExchangeMatcher createAttemptAuthenticationRequestMatcher() {
        return new PathPatternParserServerWebExchangeMatcher("/login/oauth2/code/{registrationId}");
    }

    private ReactiveOAuth2UserService<OidcUserRequest, OidcUser> getOidcUserService() {
        ResolvableType type = ResolvableType.forClassWithGenerics(ReactiveOAuth2UserService.class, OidcUserRequest.class, OidcUser.class);
        ReactiveOAuth2UserService<OidcUserRequest, OidcUser> bean = getBeanOrNull(type);
        if (bean == null) {
            return new OidcReactiveOAuth2UserService();
        }

        return bean;
    }

    private ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> getOauth2UserService() {
        ResolvableType type = ResolvableType.forClassWithGenerics(ReactiveOAuth2UserService.class, OAuth2UserRequest.class, OAuth2User.class);
        ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> bean = getBeanOrNull(type);
        if (bean == null) {
            return new DefaultReactiveOAuth2UserService();
        }

        return bean;
    }

    private Map<String, String> getLinks() {
        Iterable<ClientRegistration> registrations = getBeanOrNull(ResolvableType.forClassWithGenerics(Iterable.class, ClientRegistration.class));
        if (registrations == null) {
            return Collections.emptyMap();
        }
        Map<String, String> result = new HashMap<>();
        registrations.iterator().forEachRemaining(r -> {
            result.put("/oauth2/authorization/" + r.getRegistrationId(), r.getClientName());
        });
        return result;
    }

    private ReactiveClientRegistrationRepository getClientRegistrationRepository() {
        if (this.clientRegistrationRepository == null) {
            this.clientRegistrationRepository = getBeanOrNull(ReactiveClientRegistrationRepository.class);
        }
        return this.clientRegistrationRepository;
    }

    private ServerOAuth2AuthorizedClientRepository getAuthorizedClientRepository() {
        ServerOAuth2AuthorizedClientRepository result = this.authorizedClientRepository;
        if (result == null) {
            result = getBeanOrNull(ServerOAuth2AuthorizedClientRepository.class);
        }
        if (result == null) {
            ReactiveOAuth2AuthorizedClientService authorizedClientService = getAuthorizedClientService();
            if (authorizedClientService != null) {
                result = new AuthenticatedPrincipalServerOAuth2AuthorizedClientRepository(
                        authorizedClientService);
            }
        }
        return result;
    }

    private ReactiveOAuth2AuthorizedClientService getAuthorizedClientService() {
        ReactiveOAuth2AuthorizedClientService service = getBeanOrNull(ReactiveOAuth2AuthorizedClientService.class);
        if (service == null) {
            service = new InMemoryReactiveOAuth2AuthorizedClientService(getClientRegistrationRepository());
        }
        return service;
    }

    private <T> T getBeanOrNull(Class<T> beanClass) {
        return getBeanOrNull(ResolvableType.forClass(beanClass));
    }

    private <T> T getBeanOrNull(ResolvableType type) {
        if (this.context == null) {
            return null;
        }
        String[] names =  this.context.getBeanNamesForType(type);
        if (names.length == 1) {
            return (T) this.context.getBean(names[0]);
        }
        return null;
    }

    private Oauth2AuthoConfig() {}
}
