package com.xl.common.util;

import com.xl.pojo.Token;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author xl
 * @date 2022年01月13日 14:20
 */
@Slf4j
@Configuration
@EnableConfigurationProperties
public class TokenArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String BEARER = "Bearer ";

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().equals(Token.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        //String clientIp = RequestBaseArgumentResolver.getClientIp(webRequest.getNativeRequest(HttpServletRequest.class));
        return this.getToken(Objects.requireNonNull(nativeWebRequest.getNativeRequest(HttpServletRequest.class)));
    }

    public Token getToken(HttpServletRequest request) throws Exception {
        String tokenStr = request.getHeader("Authorization");
        //String appId = request.getParameter("appId");
        if (StringUtils.isNotBlank(tokenStr) && tokenStr.startsWith(BEARER)) {
            //String token = JWTUtils.createToken(tokenStr);

            return JWTUtilsV2.parseToken(tokenStr.replace(BEARER, ""), Token.class);
        }
        return null;
    }
}
