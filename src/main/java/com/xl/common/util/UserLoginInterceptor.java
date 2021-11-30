package com.xl.common.util;

import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.xl.common.exceptionEnum.CommonErrorCode;
import com.xl.common.util.JWTUtils;
import com.xl.exception.ExceptionCast;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @Author: X
 * @Date: 2021/5/7 20:56
 * <p>
 * 拦截器：验证用户是否登录
 */
public class UserLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //http的header中获得token
        String token = request.getHeader(JWTUtils.USER_LOGIN_TOKEN);
        String authorization = request.getHeader("Authorization");
        if (!Objects.equals(token, authorization)) {
            ExceptionCast.cast(CommonErrorCode.E_10004);
        }
        //token不存在
        if (token == null || token.equals("")) {
            throw new ApiException("请先登录");
        }
        //验证token
        String sub = JWTUtils.validateToken(token);
        if (sub == null || sub.equals("")) {
            throw new ApiException("token验证失败");
        }
        //更新token有效时间 (如果需要更新其实就是产生一个新的token)
        if (JWTUtils.isNeedUpdate(token)){
            String newToken = JWTUtils.createToken(sub);
            response.setHeader(JWTUtils.USER_LOGIN_TOKEN,newToken);
        }
        return true;
    }
}


