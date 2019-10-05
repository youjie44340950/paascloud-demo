package com.paascloud.provider.uac.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.paascloud.common.util.wrapper.WrapMapper;
import com.paascloud.common.util.wrapper.Wrapper;
import com.paascloud.provider.model.ProductDto;
import com.paascloud.provider.model.SysUserVo;
import com.paascloud.provider.service.ProductFeignApi;
import com.paascloud.provider.service.SysUserFeignApi;
import com.paascloud.provider.uac.entity.SysUser;
import com.paascloud.provider.uac.service.UserService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SysUserController  implements SysUserFeignApi {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductFeignApi productFeignApi;

    @Override
    public Wrapper<SysUserVo> queryUserInfo(String loginName) {
        SysUser one = userService.getOne(new QueryWrapper<SysUser>().eq("username", loginName));
        SysUserVo uacUser = null;
        if (one != null){
            uacUser = new SysUserVo();
            BeanUtils.copyProperties(one, uacUser);
        }
        return WrapMapper.success(uacUser);
    }

    @Override
    @GlobalTransactional
    public Wrapper testRollback(String productName) {
        SysUser sysUser = new SysUser();
        sysUser.setUsername(productName);
        userService.save(sysUser);
        ProductDto productDto = new ProductDto();
        productDto.setName(productName);
        Wrapper wrapper = productFeignApi.saveProduct(productDto);
        if (productName.equals("seata")){
            throw new RuntimeException();
        }
        return wrapper;
    }
}
