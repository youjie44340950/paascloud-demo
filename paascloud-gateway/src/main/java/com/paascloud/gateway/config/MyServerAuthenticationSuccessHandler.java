package com.paascloud.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paascloud.common.util.wrapper.WrapMapper;
import com.paascloud.common.util.wrapper.Wrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

public class MyServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    Log log = LogFactory.getLog(MyServerAuthenticationSuccessHandler.class);

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
        Wrapper wsResponse = WrapMapper.wrap(200,block.getId());
        byte[]   dataBytes={};
        ObjectMapper mapper = new ObjectMapper();
        try {
            User user=(User)authentication.getPrincipal();
            wsResponse.setResult(user);
            dataBytes=mapper.writeValueAsBytes(wsResponse);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        DataBuffer bodyDataBuffer = response.bufferFactory().wrap(dataBytes);
        return response.writeWith(Mono.just(bodyDataBuffer));
    }
}
