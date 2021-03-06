package com.paascloud.provider.service;

import com.paascloud.common.util.wrapper.Wrapper;
import com.paascloud.provider.model.SysUserVo;
import com.paascloud.provider.service.hystrix.SysUserFeignHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "paascloud-provider-uac",fallback = SysUserFeignHystrix.class)
public interface SysUserFeignApi {

    /**
     * 根据账号查询用户信息.
     *
     * @param loginName the login name
     * @return the wrapper
     */
    @GetMapping(value = "/uac/user/queryUserInfo/{loginName}")
    Wrapper<SysUserVo> queryUserInfo(@PathVariable("loginName") String loginName);

    /**
     * 添加用户
     * @param sysUser
     * @return
     */
    @PostMapping("/uac/registered")
    Wrapper registered(SysUserVo sysUser);


    /**
     * 测试 seata 事物回滚
     *
     * @param productName
     * @return the wrapper
     */
    @GetMapping(value = "/uac/seata/rollback/{productName}")
    Wrapper testRollback(@PathVariable("productName") String productName);
}
