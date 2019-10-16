package com.paascloud.gateway.config;

import com.alibaba.fastjson.JSON;
import com.paascloud.common.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

/**
 * 全局过滤器 传递当前登陆用户信息
 * @Auther: yj
 */
@Component
public class AuthSignatureFilter implements GlobalFilter, Ordered {

    @Autowired
    private RedisTemplate redisTemplate;



    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        HttpHeaders headers = exchange.getRequest().getHeaders();
        String token = headers.getFirst("token");
            if (!StringUtils.isEmpty(token)) {
                SecurityContext result = (SecurityContext) redisTemplate.opsForValue().get(token);
                if (result != null) {
                    SecurityContext context = (SecurityContext) result;
                    //向headers中放文件，记得build
                    ServerHttpRequest host = exchange.getRequest().mutate().header(UserContext.key, JSON.toJSONString(context.getAuthentication().getPrincipal())).build();
                    //将现在的request 变成 change对象
                    ServerWebExchange build = exchange.mutate().request(host).build();
                    return chain.filter(build);
                }
            }
            return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -200;
    }
}

