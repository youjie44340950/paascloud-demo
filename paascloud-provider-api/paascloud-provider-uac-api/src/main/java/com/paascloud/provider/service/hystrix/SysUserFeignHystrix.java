package com.paascloud.provider.service.hystrix;

import com.paascloud.common.util.wrapper.WrapMapper;
import com.paascloud.common.util.wrapper.Wrapper;
import com.paascloud.provider.model.SysUserVo;
import com.paascloud.provider.service.SysUserFeignApi;
import org.springframework.stereotype.Component;

@Component
public class SysUserFeignHystrix implements SysUserFeignApi {
    @Override
    public Wrapper<SysUserVo> queryUserInfo(String loginName) {
        return WrapMapper.timeOutWrap();
    }

    @Override
    public Wrapper registered(SysUserVo sysUser) {
        return WrapMapper.timeOutWrap();
    }

    @Override
    public Wrapper testRollback(String productName) {
        return WrapMapper.timeOutWrap();
    }
}
