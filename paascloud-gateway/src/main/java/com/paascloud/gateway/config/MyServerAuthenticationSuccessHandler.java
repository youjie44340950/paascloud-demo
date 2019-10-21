package com.paascloud.gateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paascloud.common.util.wrapper.WrapMapper;
import com.paascloud.common.util.wrapper.Wrapper;
import com.paascloud.provider.model.SysUserVo;
import com.paascloud.provider.service.SysUserFeignApi;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.util.Map;

public class MyServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private SysUserFeignApi sysUserFeignApi;

    Log log = LogFactory.getLog(MyServerAuthenticationSuccessHandler.class);

    public MyServerAuthenticationSuccessHandler( ){
    }

    public MyServerAuthenticationSuccessHandler(SysUserFeignApi sysUserFeignApi){
        this.sysUserFeignApi = sysUserFeignApi;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange1 = webFilterExchange.getExchange();
        Mono<WebSession> session = exchange1.getSession();
        ServerWebExchange exchange = webFilterExchange.getExchange();
        ServerHttpResponse response = exchange.getResponse();
        WebSession block = session.block();
        //设置headers
        HttpHeaders httpHeaders = response.getHeaders();
        httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
        httpHeaders.add("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        //设置body
        byte[]   dataBytes={};
        ObjectMapper mapper = new ObjectMapper();
        UserDetails user = null;
        if (authentication instanceof OAuth2AuthenticationToken){
            user = threeLogin(authentication);
        }else {
            user = (User)authentication.getPrincipal();
        }
        Wrapper wsResponse = null;
        if (user == null){
            wsResponse = WrapMapper.wrap(500, "认证失败");
        }else {
            wsResponse = WrapMapper.wrap(200, block.getId());
            wsResponse.setResult(user);
        }
        try {
            dataBytes = mapper.writeValueAsBytes(wsResponse);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        DataBuffer bodyDataBuffer = response.bufferFactory().wrap(dataBytes);
        return response.writeWith(Mono.just(bodyDataBuffer));
    }

    private UserDetails threeLogin (Authentication authentication){
        UserDetails user = null;
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken)authentication;
        String authorizedClientRegistrationId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
        OAuth2User oAuth2User = oAuth2AuthenticationToken.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String login =String.valueOf(attributes.get("login"));
        String id = String.valueOf(attributes.get("id"));
        Wrapper<SysUserVo> sysUserVoWrapper = sysUserFeignApi.queryUserInfo(login);
        if (sysUserVoWrapper.getCode() == 200 ){
            if (sysUserVoWrapper.getResult() == null) {
                SysUserVo sysUserVo = new SysUserVo();
                sysUserVo.setUsername(String.valueOf(login));
                Wrapper registered = sysUserFeignApi.registered(sysUserVo);
                if (registered.getCode() == 200) {
                    user = User.builder().username(login).password("").roles("user").build();
                }
            }else {
                user = User.builder().username(login).password("").roles("user").build();
            }
        }
        return user;
    }
}
