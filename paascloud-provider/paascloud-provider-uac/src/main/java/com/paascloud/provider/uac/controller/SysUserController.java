package com.paascloud.provider.uac.controller;

import com.paascloud.common.util.wrapper.WrapMapper;
import com.paascloud.common.util.wrapper.Wrapper;
import com.paascloud.provider.uac.dao.SysUserMapper;
import com.paascloud.provider.uac.entity.SysUser;
import com.paascloud.provider.uac.service.UserService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class SysUserController {

    @Autowired
    private UserService userService;

    @GetMapping("info")
    @GlobalTransactional
    public Wrapper getInfo(String id){
        SysUser user = null;
        if (user == null){
            user = new SysUser();
        }
        user.setUsername("youjie");
        userService.save(user);
        if (id.equals("1")){
            throw new RuntimeException();
        }
        return WrapMapper.wrap(200,"成功",user);
    }
}
