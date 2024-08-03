package com.czh.usercenter.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.czh.usercenter.common.BaseResponse;
import com.czh.usercenter.common.ErrorCode;
import com.czh.usercenter.common.ResultUtils;
import com.czh.usercenter.exception.BusinessException;
import com.czh.usercenter.mapper.request.UserLoginRequest;
import com.czh.usercenter.mapper.request.UserRegisterRequest;
import com.czh.usercenter.model.domain.User;
import com.czh.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;


import static com.czh.usercenter.constant.UserConstants.ADMIN_ROLE;
import static com.czh.usercenter.constant.UserConstants.USER_LOGIN_STATE;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
public class UserController implements Serializable {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException((ErrorCode.NULL_ERROR));
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException((ErrorCode.NULL_ERROR));
        }
        long id = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(id);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException((ErrorCode.NULL_ERROR));
        }

        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException((ErrorCode.NULL_ERROR));
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        //鉴权 仅管理员可查询
        if (!isAdmin(request)) {
            throw new BusinessException((ErrorCode.NO_AUTH));
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        userList = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(userList);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody Long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException((ErrorCode.NULL_ERROR));
        }
        if (id <= 0) {
            throw new BusinessException((ErrorCode.NULL_ERROR));
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    @GetMapping("/currentUser")
    public BaseResponse<User> currentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if(currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long id = currentUser.getId();
        User user = userService.getById(id);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        if (request == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        userService.userLogout(request);
    }

    public Boolean isAdmin(HttpServletRequest request) {
        Object userInfo = request.getSession().getAttribute(
                USER_LOGIN_STATE);
        User user = (User) userInfo;
        if (user == null || user.getUserRole() != ADMIN_ROLE) {
            throw new BusinessException((ErrorCode.NULL_ERROR));
        }
        return true;
    }
}
