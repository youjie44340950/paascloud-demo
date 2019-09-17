package com.paascloud.gateway.config;

import org.springframework.security.core.userdetails.*;
import reactor.core.publisher.Mono;

/**
 *
 */

public class MyAuthenticationProvider implements ReactiveUserDetailsService {

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        UserDetails result =null;
        if (username.equals("youjie")){
            result = User.builder().username("youjie").password("123").roles("admin").build();
        }
        return result == null ? Mono.empty() : Mono.just(User.withUserDetails(result).build());
    }

}
