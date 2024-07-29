package com.czh.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czh.usercenter.model.domain.User;
import com.czh.usercenter.service.UserService;
import com.czh.usercenter.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author 86137
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-07-30 04:11:16
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




