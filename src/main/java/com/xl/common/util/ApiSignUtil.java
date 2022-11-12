package com.xl.common.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import com.xl.pojo.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * api签名工具类
 *
 * @author User
 * @date 2019/02/26
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiSignUtil {

    private static final String CONTENT_CHARSET = "UTF-8";

    private static final String HMAC_ALGORITHM = "HmacSHA1";

    private static final String TENCENT_GATEWAY_SECRET_KEY = "eseId:%s:gatewaySecret";

    //private final RedisUtils redisUtils;
//
//    public static String getTimeStr() {
//        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
//        return sdf.format(new Date());
//    }

    public static Date getDate(String timeStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.parse(timeStr);
    }

    private static String gatewaySecretKey(String eseId) {
        return String.format(TENCENT_GATEWAY_SECRET_KEY, eseId);
    }

    /**
     * 验证授权参数
     *
     * @param httpRequest HttpServletRequest
     * @param secretId    secretId
     * @param secretKey   secretKey
     * @throws Exception Exception
     */
    public static void checkAuthorization(HttpServletRequest httpRequest, String secretId, String secretKey) throws Exception {
        String authorization = httpRequest.getHeader(Constants.AUTHORIZATION);
        String timeStr = httpRequest.getHeader(Constants.DATE);
        String source = httpRequest.getHeader(Constants.SOURCE);
        checkAuthorization(authorization, timeStr, source, secretId, secretKey);
    }

    /**
     * 验证授权参数
     *
     * @param authorization authorization
     * @param timeStr       timeStr
     * @param source        source
     * @param secretId      secretId
     * @param secretKey     secretKey
     * @throws Exception Exception
     */
    public static void checkAuthorization(String authorization, String timeStr, String source, String secretId, String secretKey)
            throws Exception {

        if (StringUtils.isEmpty(authorization) || StringUtils.isEmpty(timeStr)) {
            //throw new BadRequestException(OpenApiCode.CODE_42022, authorization, timeStr, source, secretId, secretKey);
        }
        Date date = getDate(timeStr);
        Date fifteenMinutesAgo = DateUtils.addMinutes(new Date(), -15);
        if (DateUtils.truncatedCompareTo(fifteenMinutesAgo, date, Calendar.MINUTE) > 0) {
            //throw new BadRequestException(OpenApiCode.CODE_42023, authorization, timeStr, source, secretId, secretKey);
        }
        String auth = ApiSignUtilBase.getAuth(secretId, secretKey, timeStr, source);
        if (!Objects.equals(auth, authorization)) {
            //throw new BadRequestException(OpenApiCode.CODE_42020, authorization, timeStr, source, secretId, secretKey);
        }
    }

//    /**
//     * @param secretId  secretId
//     * @param secretKey secretKey
//     * @param timeStr   timeStr
//     * @param source    source
//     * @return 签名
//     * @throws Exception Exception
//     */
//    public static String getAuth(String secretId, String secretKey, String timeStr, String source) throws Exception {
//
//        // get signStr
//        StringBuilder sign = new StringBuilder("date: ").append(timeStr);
//        if (StringUtils.isNotEmpty(source)) {
//            sign.append("\n").append("source: ").append(source);
//        }
//
//        // get sig
//        Mac mac1 = Mac.getInstance(HMAC_ALGORITHM);
//        byte[] hash;
//        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(CONTENT_CHARSET), mac1.getAlgorithm());
//        mac1.init(secretKeySpec);
//        hash = mac1.doFinal(sign.toString().getBytes(CONTENT_CHARSET));
//        String sig = Base64.encode(hash);
//
//        StringBuilder auth = new StringBuilder("hmac id=\"").append(secretId).append("\", algorithm=\"hmac-sha1\"");
//        if (StringUtils.isNotEmpty(source)) {
//            auth.append(", headers=\"date source\", signature=\"");
//        } else {
//            auth.append(", headers=\"date\", signature=\"");
//        }
//        return auth.append(sig).append("\"").toString();
//    }

    //public void checkBlacklist(String openId, String ip) {
    //    checkBlacklist(openId, ip, null, null);
    //}

    //public static HttpHeaders getSignedHeader(String body, String secretId, String secretKey, String nameSpaceCode, boolean gray) {
    //    try {
    //        String bodyMd5 = DigestUtils.md5DigestAsHex(body.getBytes(StandardCharsets.UTF_8)).toLowerCase();
    //        String nonce = RandomStringUtils.random(4, true, true);
    //        String dateTime = getGmt();
    //        String sign = getSign(dateTime, bodyMd5, nonce, secretId, secretKey);
    //
    //        HttpHeaders headers = new HttpHeaders();
    //        headers.setContentType(MediaType.APPLICATION_JSON);
    //        headers.add("X-NameSpace-Code", nameSpaceCode);
    //        headers.add("X-MicroService-Name", "uma-shop-api-web");
    //        if (gray) {
    //            headers.set("X-MicroService-Name", "uma-shop-api-web-gray1");
    //        }
    //        headers.add("X-Date", dateTime);
    //        headers.add("x-sr-authorization", bodyMd5);
    //        headers.add("x-sr-nonce", nonce);
    //        headers.add("x-sr-secretid", secretId);
    //        headers.add("Authorization", sign);
    //
    //        return headers;
    //    } catch (Throwable throwable) {
    //        //throw new InternalException(SystemError.INTERNAL_SERVER_ERROR);
    //    }
    //}

    public static String getGmt() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat greenwichDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        // 时区设为格林尼治
        greenwichDate.setTimeZone(TimeZone.getTimeZone("GMT"));
        return greenwichDate.format(cal.getTime());
    }

    public static String getSign(String dateTime, String bodyMd5, String nonce, String secretId, String secretKey)
            throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        final String encoding = "UTF-8";
        final String algorithm = "HmacSHA1";
        String auth = "hmac id=\"" + secretId
                + "\", algorithm=\"hmac-sha1\", headers=\"x-date x-sr-authorization x-sr-nonce x-sr-secretid\", signature=\"";
        String signStr = "x-date: " + dateTime + "\n" + "x-sr-authorization: " + bodyMd5 + "\n" + "x-sr-nonce: " + nonce + "\n"
                + "x-sr-secretid: " + secretId;

        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(secretKey.getBytes(encoding), algorithm));
        byte[] signData = mac.doFinal(signStr.getBytes(encoding));
        String sign = new String(java.util.Base64.getEncoder().encode(signData));
        sign = auth + sign + "\"";
        return sign;
    }

    /**
     * 第三方验证授权参数
     *
     * @param httpRequest HttpServletRequest
     * @param secretId    secretId
     * @param secretKey   secretKey
     * @param data   data
     */
    public static void thirdAuthorization(HttpServletRequest httpRequest, String secretId, String secretKey, Object data) {
        String nonce = httpRequest.getHeader(Constants.NONCE);
        String timeStr = httpRequest.getHeader(Constants.TIMESTAMP);
        String sign = httpRequest.getHeader(Constants.SIGN);

        Date date = new Date(Long.parseLong(timeStr));

        Date fifteenMinutesAgo = DateUtils.addMinutes(new Date(), -15);
        if (DateUtils.truncatedCompareTo(fifteenMinutesAgo, date, Calendar.MINUTE) > 0) {
            //throw new BadRequestException(OpenApiCode.CODE_42023, sign, timeStr, nonce, date, secretId, secretKey);
        }

        String signStr = new StringBuilder().append("secretId=").append(secretId).append("&secretKey=").append(secretKey)
                .append("&timestamp=").append(timeStr).append("&nonce=").append(nonce).append("&data=")
                .append(JacksonUtils.write2JsonString(data)).toString();
        String mySign = getShaValue(signStr);
        if (!Objects.equals(sign, mySign)) {
            //ElkLoggerUtils.logOut(log, LogLevel.INFO, OpenApiCode.FAIL, "findPromotionByOutercode", nonce, timeStr, sign, signStr,
            //        mySign);
            //throw new BadRequestException(OpenApiCode.CODE_40911);
        }
    }

    private static String getShaValue(String value) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            byte[] md5ValueByteArray = messageDigest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexValue = new StringBuilder();
            for (byte b : md5ValueByteArray) {
                int val = ((int) b) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString().toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
