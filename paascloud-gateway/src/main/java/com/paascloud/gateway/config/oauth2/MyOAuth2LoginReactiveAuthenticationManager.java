package com.paascloud.gateway.config.oauth2;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.ReactiveOAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Map;

public class MyOAuth2LoginReactiveAuthenticationManager implements ReactiveAuthenticationManager {
    private final ReactiveAuthenticationManager authorizationCodeManager;

    private final ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> userService;

    private GrantedAuthoritiesMapper authoritiesMapper = (authorities -> authorities);

    public MyOAuth2LoginReactiveAuthenticationManager(
            ReactiveOAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient,
            ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> userService) {
        super();
        Assert.notNull(accessTokenResponseClient, "accessTokenResponseClient cannot be null");
        Assert.notNull(userService, "userService cannot be null");
        this.authorizationCodeManager =new MyOAuth2AuthorizationCodeReactiveAuthenticationManager(accessTokenResponseClient);
        this.userService = userService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.defer(() -> {
            OAuth2AuthorizationCodeAuthenticationToken token = (OAuth2AuthorizationCodeAuthenticationToken) authentication;

            // Section 3.1.2.1 Authentication Request - https://openid.net/specs/openid-connect-core-1_0.html#AuthRequest
            // scope REQUIRED. OpenID Connect requests MUST contain the "openid" scope value.
            if (token.getAuthorizationExchange()
                    .getAuthorizationRequest().getScopes().contains("openid")) {
                // This is an OpenID Connect Authentication Request so return null
                // and let OidcAuthorizationCodeReactiveAuthenticationManager handle it instead once one is created
                // FIXME: Once we create OidcAuthorizationCodeReactiveAuthenticationManager uncomment below
//				return Mono.empty();
            }

            return this.authorizationCodeManager.authenticate(token)
                    .onErrorMap(OAuth2AuthorizationException.class, e -> new OAuth2AuthenticationException(e.getError(), e.getError().toString()))
                    .cast(OAuth2AuthorizationCodeAuthenticationToken.class)
                    .flatMap(this::onSuccess);
        });
    }

    private Mono<OAuth2LoginAuthenticationToken> onSuccess(OAuth2AuthorizationCodeAuthenticationToken authentication) {
        OAuth2AccessToken accessToken = authentication.getAccessToken();
        Map<String, Object> additionalParameters = authentication.getAdditionalParameters();
        OAuth2UserRequest userRequest = new OAuth2UserRequest(authentication.getClientRegistration(), accessToken, additionalParameters);
        return this.userService.loadUser(userRequest)
                .map(oauth2User -> {
                    Collection<? extends GrantedAuthority> mappedAuthorities =
                            this.authoritiesMapper.mapAuthorities(oauth2User.getAuthorities());

                    OAuth2LoginAuthenticationToken authenticationResult = new OAuth2LoginAuthenticationToken(
                            authentication.getClientRegistration(),
                            authentication.getAuthorizationExchange(),
                            oauth2User,
                            mappedAuthorities,
                            accessToken,
                            authentication.getRefreshToken());
                    return authenticationResult;
                });
    }
}
