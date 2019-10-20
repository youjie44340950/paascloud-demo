package com.paascloud.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;


/**
 * 将认证信息存入redis
 * @Auther: yj
 */
@Component
public class MyServerSecurityContextRepository implements ServerSecurityContextRepository {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        WebSession block = exchange.getSession().block();
        redisTemplate.opsForValue().set(block.getId(),context);
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String token = headers.getFirst("token");
        try {
            if (!StringUtils.isEmpty(token)) {
                SecurityContext context = (SecurityContext) redisTemplate.opsForValue().get(token);
                return Mono.justOrEmpty(context);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return Mono.empty();
    }
}
