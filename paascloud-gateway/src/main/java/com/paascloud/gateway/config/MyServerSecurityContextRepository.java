package com.paascloud.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

@Configuration
public class MyServerSecurityContextRepository implements ServerSecurityContextRepository {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        Mono<Void> voidMono = exchange.getSession()
                .doOnNext(session -> {
                    if (context == null) {
                        redisTemplate.delete(session.getId());
                    }
                })
                .flatMap(session -> {
                    session.changeSessionId();
                    if (context != null){
                        redisTemplate.opsForValue().set(session.getId(),context);
                    }
                    return Mono.empty();
                });
        return voidMono;
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        return exchange.getSession()
                .map(WebSession::getId)
                .flatMap( sessionId -> {
                    SecurityContext context = (SecurityContext) redisTemplate.opsForValue().get(sessionId);
                    return Mono.justOrEmpty(context);
                });
    }
}
