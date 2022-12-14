package com.xl.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.xl.common.exceptionEnum.CommonErrorCode;
import com.xl.common.util.JWTUtils;
import com.xl.common.util.JWTUtilsV2;
import com.xl.dao.UserTokenDao;
import com.xl.dao.UserDao;
import com.xl.exception.ExceptionCast;
import com.xl.pojo.Token;
import com.xl.pojo.User;
import com.xl.pojo.UserToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

/**
 * @author xl
 * @date 2021年10月26日 14:23
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;

    private final UserTokenDao userTokenDao;

    public String login(String phone, String password) {

        User selectUser = getUserByPhone(phone);

        if (ObjectUtils.isEmpty(selectUser)) {
            ExceptionCast.cast(CommonErrorCode.E_10001);
        }

        if (!Objects.equals(password,selectUser.getPassword())) {
            ExceptionCast.cast(CommonErrorCode.E_10002);
        }

        //ExceptionCast.cast(CommonErrorCode.SUCCESS);

        return getToken(selectUser);

    }

    private User getUserByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        return userDao.selectOne(wrapper);
    }

    @Transactional
    public String registry(User user) {
        //phone是除id外的唯一标志 需要进行检查
        if (user.getPhone() == null || user.getPhone().equals("")) {
            throw new ApiException("手机号不合法");
        }
        User selectUser = getUserByPhone(user.getPhone());

        if (ObjectUtils.isEmpty(selectUser)) {
            //注册用户
            user.setUserId(UUID.randomUUID().toString());
            int count = userDao.insert(user);
            if (count < 1){
                throw new ApiException("注册异常");
            }
        }

        User insertUser = getUserByPhone(user.getPhone());

        //将userId存入token中
        String s = getToken(insertUser);

        UserToken userToken = new UserToken();
        userToken.setToken(s);
        userToken.setUserId(insertUser.getUserId());
        int insert = userTokenDao.insert(userToken);
        if (insert < 1){
            throw new ApiException("token保存失败");
        }
        return s;
    }

    private String getToken(User insertUser) {
        Token token = new Token();
        token.setUserId(insertUser.getId());
        token.setUserTokenId(insertUser.getId());
        return JWTUtilsV2.getToken(token, 10000L);
    }

    public User getUserInfo(Long userId) {

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserId,userId);
        return userDao.selectOne(queryWrapper);

    }
}
