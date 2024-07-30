package com.czh.usercenter.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;


    @Test
    void testUserRegister() {
        String userAccount = "chen";
        String password = "";
        String checkPassword = "12345678";
        long result = userService.userRegister(userAccount, password, checkPassword);
        Assert.assertEquals(-1, result);
        userAccount = "cz";
        result = userService.userRegister(userAccount, password, checkPassword);
        Assert.assertEquals(-1, result);
        userAccount = "chen";
        password = "12";
        result = userService.userRegister(userAccount, password, checkPassword);
        Assert.assertEquals(-1, result);
        userAccount = "ch en";
        result = userService.userRegister(userAccount, password, checkPassword);
        Assert.assertEquals(-1, result);
        userAccount = "chen";
        password = "12345678";
        checkPassword = "123456789";
        result = userService.userRegister(userAccount, password, checkPassword);
        Assert.assertEquals(-1, result);
        userAccount = "chenzhihao";
        result = userService.userRegister(userAccount, password, checkPassword);
        Assert.assertEquals(-1, result);
        userAccount = "chen";
        password = "12345678";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, password, checkPassword);
        Assert.assertTrue(result > 0);


    }
}
