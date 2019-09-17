package com.paascloud.provider.uac.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paascloud.provider.uac.dao.SysUserMapper;
import com.paascloud.provider.uac.entity.SysUser;
import com.paascloud.provider.uac.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<SysUserMapper,SysUser> implements UserService {


}
