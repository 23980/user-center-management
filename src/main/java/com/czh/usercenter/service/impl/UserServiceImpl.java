package com.czh.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czh.usercenter.model.domain.User;
import com.czh.usercenter.service.UserService;
import com.czh.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.czh.usercenter.constant.UserConstants.USER_LOGIN_STATE;

/**
* @author 86137
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-07-30 04:11:16
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    /**
     * 盐值： 混淆密码
     */
    private static final String SALT = "czh";





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
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);
        if (!saveResult){
            return -1;
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 非空判断
        if (StringUtils.isAnyBlank(userAccount, userPassword)){
            return null;
        }
        // 账号长度判断
        if (userAccount.length() < 4){
            return null;
        }
        // 密码长度判断
        if (userPassword.length() < 8 ){
            return null;
        }
        // 账号不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()){
            return null;
        }
        // 密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        //
        if (user == null){
            log.info("user login failed, userAccount or userPassword is wrong");
            return null;
        }
        // 用户脱敏
        User satetyUser = getSafetyUser(user);

        // 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, satetyUser);
        return satetyUser;
    }

    @Override
    public User getSafetyUser(User originUser){
        User satetyUser = new User();
        satetyUser.setId(originUser.getId());
        satetyUser.setUsername(originUser.getUsername());
        satetyUser.setUserAccount(originUser.getUserAccount());
        satetyUser.setAvatarUrl(originUser.getAvatarUrl());
        satetyUser.setGender(originUser.getGender());
        satetyUser.setEmail(originUser.getEmail());
        satetyUser.setUserStatus(originUser.getUserStatus());
        satetyUser.setPhone(originUser.getPhone());
        satetyUser.setCreateTime(originUser.getCreateTime());
        return satetyUser;
        }
}




