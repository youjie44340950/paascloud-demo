package com.paascloud.gateway.config.oauth2;

import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerAuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class MyServerAuthorizationRequestRepository implements ServerAuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private final ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver;

    public static final String DEFAULT_REGISTRATION_ID_URI_VARIABLE_NAME = "registrationId";

    public static final String DEFAULT_AUTHORIZATION_REQUEST_PATTERN = "/login/oauth2/code/{" + DEFAULT_REGISTRATION_ID_URI_VARIABLE_NAME
            + "}";

    public MyServerAuthorizationRequestRepository(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        this.authorizationRequestResolver = new DefaultServerOAuth2AuthorizationRequestResolver(clientRegistrationRepository,
        new PathPatternParserServerWebExchangeMatcher(
                DEFAULT_AUTHORIZATION_REQUEST_PATTERN));
    }

    @Override
    public Mono<OAuth2AuthorizationRequest> loadAuthorizationRequest(ServerWebExchange exchange) {
        return null;
    }

    @Override
    public Mono<Void> saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, ServerWebExchange exchange) {
        return null;
    }

    @Override
    public Mono<OAuth2AuthorizationRequest> removeAuthorizationRequest(ServerWebExchange exchange) {
        Mono<OAuth2AuthorizationRequest> resolve = this.authorizationRequestResolver.resolve(exchange);
        return resolve;
    }
}
