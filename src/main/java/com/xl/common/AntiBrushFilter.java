package com.xl.common;

import com.xl.common.exceptionEnum.CommonErrorCode;
import com.xl.common.util.DesUtils;
import com.xl.common.util.JacksonUtils;
import com.xl.exception.ExceptionCast;
import lombok.SneakyThrows;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author xl
 * @date 2021年10月27日 10:04
 */
@Component
@ConfigurationProperties(prefix = "des")
public class AntiBrushFilter implements Filter {

    public static String encryptKey;

    public void setEncryptKey(String encryptKey) {
        AntiBrushFilter.encryptKey = encryptKey;
    }

    /* 这里设置不被拦截的请求路径 */
    //private static final List<String> unFilterUrlList = Collections.singletonList("/user/registry");

    /* 判断请求路径是否为不拦截的请求路径 */
    //private boolean isFilter(String url){
    //    for(String s: unFilterUrlList) {
    //        if(url.contains(s)) {
    //            return false;
    //        }
    //    }
    //    return true;
    //}


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String requestURI = httpRequest.getRequestURI();

        String antibrush = httpRequest.getHeader("AntiBrush");
        Map<String, String[]> parameterMap = httpRequest.getParameterMap();

        Map<String, Object> signMap = new HashMap<>();

        if (ObjectUtils.isNotEmpty(parameterMap)) {
            if (MapUtils.isNotEmpty(parameterMap)) {
                for (String key : parameterMap.keySet()) {
                    String val = parameterMap.get(key)[0];
                    signMap.put(key, StringUtils.isNotBlank(val) ? val.replace(' ', '+') : val);
                }
            }
        }

        if (StringUtils.isEmpty(antibrush)) {
            ExceptionCast.cast(CommonErrorCode.E_10005);
        }

        String signStr = prepareSignStr(signMap);

        if (Objects.equals(httpRequest.getMethod(),"GET") && Objects.equals(requestURI,"/user/login")) {
            if (!StringUtils.equals(antibrush, DesUtils.encryptDES(signStr, encryptKey))) {
                ExceptionCast.cast(CommonErrorCode.E_10003);
            }
        }

        filterChain.doFilter(httpRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

    private static String prepareSignStr(Map map) {
        StringBuffer strBuffer = new StringBuffer();
        try {
            TreeMap<String, Object> treeMap = new TreeMap<>(map);
            treeMap.entrySet().forEach(i -> {
                if (i.getValue() == null || i.getValue() == "" || i.getValue() instanceof Map) {
                    return;
                }
                if (i.getValue() instanceof List) {
                    strBuffer.append(i.getKey()).append(JacksonUtils.write2JsonString(i.getValue()));
                } else {
                    strBuffer.append(i.getKey()).append(i.getValue());
                }

            });
            return strBuffer.toString();
        } catch (Exception e) {
            ExceptionCast.cast(CommonErrorCode.E_10006);
        }
        return strBuffer.toString();
    }
}
