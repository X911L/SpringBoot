package com.xl.common.util;

import com.xl.common.config.BodyReaderHttpServletRequestWrapper;
import com.xl.common.exceptionEnum.CommonErrorCode;
import com.xl.exception.ExceptionCast;
import com.xl.pojo.Constants;
import lombok.SneakyThrows;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
    private static final List<String> unFilterUrlList = Collections.singletonList("/user/registry,/user/login");

    /* 判断请求路径是否为不拦截的请求路径 */
    private boolean isFilter(String url){
        for(String s: unFilterUrlList) {
            if(url.contains(s)) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String requestURI = httpRequest.getRequestURI();

        //HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletRequest requestWrapper = new BodyReaderHttpServletRequestWrapper(httpRequest);

        if (!isFilter(requestURI)) {
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

            //if (Objects.equals(httpRequest.getMethod(),"GET") && Objects.equals(requestURI,"/user/login")) {
            //    if (!StringUtils.equals(antibrush, DesUtils.encryptDES(signStr, encryptKey))) {
            //        ExceptionCast.cast(CommonErrorCode.E_10003);
            //    }
            //}
        }
        String ip = ServletRequestHelper.getClientIp(httpRequest);

        authorization(requestWrapper, ip);

        filterChain.doFilter(httpRequest, servletResponse);
    }

    private void authorization(HttpServletRequest httpRequest, String ip) throws Exception {
        String authorization = httpRequest.getHeader(Constants.AUTHORIZATION);
        String timeStr = httpRequest.getHeader(Constants.DATE);
        String source = httpRequest.getHeader(Constants.SOURCE);
        if (StringUtils.isBlank(authorization) || StringUtils.isBlank(timeStr) || StringUtils.isBlank(source)) {
            //throw new BadRequestException(OpenApiCode.INTERNAL_EXCEPTION, "认证时，Headers必须包含 Authorization,Source,Date");
        }

        Date date = ApiSignUtil.getDate(timeStr);
        Date fifteenMinutesAgo = DateUtils.addMinutes(new Date(), -15);
        if (DateUtils.truncatedCompareTo(fifteenMinutesAgo, date, Calendar.MINUTE) > 0) {
            //throw new BadRequestException(OpenApiCode.CODE_42023, timeStr, date, secretId, secretKey);
        }

        String requestBody = IOUtils.toString(httpRequest.getReader());
        if (StringUtils.isBlank(requestBody)) {
            //throw new BadRequestException(OpenApiCode.INTERNAL_EXCEPTION, "请求体不可为空");
        }
        String secretId = "id";
        String secretKey = "key";
        String mySign = getSign(secretId, secretKey, timeStr, source, requestBody);
        if (!Objects.equals(authorization, mySign)) {
            //log.info("ThirdPartyAuthFilter.authorization requestBody:{},authorization:{},timeStr:{},source:{},mysign:{}", requestBody, authorization, timeStr, source, mySign);
            //throw new BadRequestException(OpenApiCode.CODE_40911, timeStr, mySign);
        }
        //httpRequest.setAttribute(CommonConstant.ESE_ID, secretDTO.getEseId());

    }

    private String getSign(String secretId, String secretKey, String timeStr, String source, String body) throws Exception {
        String bodyMd5 = DigestUtils.md5DigestAsHex(body.getBytes(StandardCharsets.UTF_8)).toLowerCase();
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), mac.getAlgorithm());
        mac.init(secretKeySpec);
        byte[] signData = mac.doFinal(
                ("date: " + timeStr + "\n" + "source: " + source + "\n" + "body: " + bodyMd5).getBytes(StandardCharsets.UTF_8));
        String sig = new String(Base64.getEncoder().encode(signData));
        return "hmac id=\"" + secretId + "\", algorithm=\"hmac-sha1\"" + ", headers=\"date source\", signature=\"" + sig + "\"";
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
