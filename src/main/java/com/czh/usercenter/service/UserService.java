package com.czh.usercenter.service;

import com.czh.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 86137
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-07-30 04:11:16
*/
public interface UserService extends IService<User> {

    /**
     *
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

}
