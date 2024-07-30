package com.czh.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czh.usercenter.model.domain.User;
import com.czh.usercenter.service.UserService;
import com.czh.usercenter.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @author 86137
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-07-30 04:11:16
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {

        // 非空判断
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)){
            return -1;
        }
        // 账号长度判断
        if (userAccount.length() < 4){
            return -1;
        }
        // 密码长度判断
        if (userPassword.length() < 8 || checkPassword.length() < 8){
            return -1;
        }
        // 账号不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()){
            return -1;
        }
        // 账号不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        if (userMapper.selectCount(queryWrapper) > 0 ){
            return -1;
        }
        if (!userPassword.equals(checkPassword)){
            return -1;
        }
        // 密码加密
        final String SALT = "czh";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(userPassword);
        boolean saveResult = this.save(user);
        if (!saveResult){
            return -1;
        }
        return user.getId();
    }
}




