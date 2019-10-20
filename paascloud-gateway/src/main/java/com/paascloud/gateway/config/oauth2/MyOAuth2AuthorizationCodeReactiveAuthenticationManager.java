package com.paascloud.gateway.config.oauth2;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationToken;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.ReactiveOAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class MyOAuth2AuthorizationCodeReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final ReactiveOAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient;

    public MyOAuth2AuthorizationCodeReactiveAuthenticationManager(
            ReactiveOAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient) {
        Assert.notNull(accessTokenResponseClient, "accessTokenResponseClient cannot be null");
        this.accessTokenResponseClient = accessTokenResponseClient;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.defer(() -> {
            OAuth2AuthorizationCodeAuthenticationToken token = (OAuth2AuthorizationCodeAuthenticationToken) authentication;

//            OAuth2AuthorizationExchangeValidator.validate(token.getAuthorizationExchange());

            OAuth2AuthorizationCodeGrantRequest authzRequest = new OAuth2AuthorizationCodeGrantRequest(
                    token.getClientRegistration(),
                    token.getAuthorizationExchange());

            return this.accessTokenResponseClient.getTokenResponse(authzRequest)
                    .map(onSuccess(token));
        });
    }

    private Function<OAuth2AccessTokenResponse, OAuth2AuthorizationCodeAuthenticationToken> onSuccess(OAuth2AuthorizationCodeAuthenticationToken token) {
        return accessTokenResponse -> {
            ClientRegistration registration = token.getClientRegistration();
            OAuth2AuthorizationExchange exchange = token.getAuthorizationExchange();
            OAuth2AccessToken accessToken = accessTokenResponse.getAccessToken();
            OAuth2RefreshToken refreshToken = accessTokenResponse.getRefreshToken();
            return new OAuth2AuthorizationCodeAuthenticationToken(registration, exchange, accessToken, refreshToken, accessTokenResponse.getAdditionalParameters());
        };
    }
}
