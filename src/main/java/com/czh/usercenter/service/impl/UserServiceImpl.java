package com.czh.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czh.usercenter.common.ErrorCode;
import com.czh.usercenter.exception.BusinessException;
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
        implements UserService {

    /**
     * 盐值： 混淆密码
     */
    private static final String SALT = "czh";



    @Resource
    private UserMapper userMapper;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {



        // 非空判断
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException((ErrorCode.NULL_ERROR));
        }

        // 账号长度判断
        if (userAccount.length() < 4) {
            throw new BusinessException((ErrorCode.PARAMS_ERROR),"账号长度不能小于4");
        }

        // 密码长度判断
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException((ErrorCode.PARAMS_ERROR),"密码长度不能小于8");
        }
        // 账号不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException((ErrorCode.PARAMS_ERROR),"账号不能包含特殊字符");
        }
        // 账号不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException((ErrorCode.PARAMS_ERROR),"账号已存在");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException((ErrorCode.PARAMS_ERROR),"两次输入密码不一致");
        }
        // 密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 非空判断
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException((ErrorCode.NULL_ERROR));
        }
        // 账号长度判断
        if (userAccount.length() < 4) {
            throw new BusinessException((ErrorCode.PARAMS_ERROR),"账号长度不能小于4");
        }
        // 密码长度判断
        if (userPassword.length() < 8) {
            throw new BusinessException((ErrorCode.PARAMS_ERROR),"密码长度不能小于8");
        }
        // 账号不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException((ErrorCode.PARAMS_ERROR),"账号不能包含特殊字符");
        }
        // 密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        //
        if (user == null) {
            log.info("user login failed, userAccount or userPassword is wrong");
            throw new BusinessException((ErrorCode.NULL_ERROR),"用户不存在");
        }
        // 用户脱敏
        User satetyUser = getSafetyUser(user);

        // 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, satetyUser);
        return satetyUser;
    }

    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setCreateTime(originUser.getCreateTime());
        return safetyUser;
    }

    @Override
    public void userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
    }
}




