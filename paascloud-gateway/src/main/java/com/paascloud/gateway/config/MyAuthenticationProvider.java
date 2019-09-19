package com.paascloud.gateway.config;

import com.paascloud.common.util.wrapper.Wrapper;
import com.paascloud.provider.model.SysUserVo;
import com.paascloud.provider.service.SysUserFeignApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class MyAuthenticationProvider implements ReactiveUserDetailsService {

    @Autowired
    private SysUserFeignApi sysUserFeignApi;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        UserDetails result =null;
        Wrapper<SysUserVo> sysUserVoWrapper = sysUserFeignApi.queryUserInfo(username);
        if (sysUserVoWrapper.getCode() == 200 && sysUserVoWrapper.getResult() != null){
            SysUserVo result1 = sysUserVoWrapper.getResult();
            result = User.builder().username(username).password(result1.getPassword()).roles("admin").build();
        }
        return result == null ? Mono.empty() : Mono.just(User.withUserDetails(result).build());
    }

}
