package com.xl.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.xml.internal.security.keys.KeyUtils;
import com.xl.pojo.JwtHeader;
import com.xl.pojo.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: xiang
 * @Date: 2021/5/11 21:11
 * <p>
 * JwtToken生成的工具类
 * JWT token的格式：header.payload.signature
 * header的格式（算法、token的类型）,默认：{"alg": "HS512","typ": "JWT"}
 * payload的格式 设置：（用户信息、创建时间、生成时间）
 * signature的生成算法：
 * HMACSHA512(base64UrlEncode(header) + "." +base64UrlEncode(payload),secret)
 */

@Component
@ConfigurationProperties(prefix = "jwt")
public class JWTUtils {

    //定义token返回头部
    public static String header;

    //token前缀
    public static String tokenPrefix;

    //签名密钥
    public static String secret;

    //有效期
    public static long expireTime;

    //存进客户端的token的key名
    public static final String USER_LOGIN_TOKEN = "USER_LOGIN_TOKEN";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final static Map<Integer, String> DIGEST_KEY_MAP = new HashMap<>();

    public static <T> T parseToken(String token, Class<T> clazz) throws Exception {
        if (StringUtils.isEmpty(token)) {
            throw new RuntimeException("token cannot be null");
        }
        //Integer kid = parseHeader(token).getKid();
        //if (kid == null) {
        //    kid = 0;
        //}
        //String base64Security = DIGEST_KEY_MAP.get(kid);
//        Claims claims = parseJWT(token);
//
//        if (claims != null) {
//            String s = MAPPER.writeValueAsString(claims);
//            return MAPPER.readValue(s, clazz);
////            BeanUtils.populate(obj, claims);
//        }
        Token entity = JacksonUtils.readJson2Entity(token, Token.class);
        return MAPPER.readValue(token, clazz);
        //return null;
    }

    public static Claims parseJWT(String jsonWebToken) {
        return Jwts.parser().parseClaimsJws(jsonWebToken).getBody();
    }

    //public static Claims parseJWT(String jsonWebToken, String base64Security) {
    //    return Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(base64Security))
    //            .parseClaimsJws(jsonWebToken).getBody();
    //}

    private static JwtHeader parseHeader(String jsonWebToken) throws IOException {
        String[] params = StringUtils.split(jsonWebToken, ".");
        String header = new String(Base64.decodeBase64(params[0]));
        return MAPPER.readValue(header, JwtHeader.class);
    }

    public void setHeader(String header) {
        JWTUtils.header = header;
    }

    public void setTokenPrefix(String tokenPrefix) {
        JWTUtils.tokenPrefix = tokenPrefix;
    }

    public void setSecret(String secret) {
        JWTUtils.secret = secret;
    }

    public void setExpireTime(int expireTimeInt) {
        JWTUtils.expireTime = expireTimeInt*1000L*60;
    }

    /**
     * 创建TOKEN
     * @param sub
     * @return
     */
    public static String createToken(Object sub) {

        Map<String, Object> header = new HashMap<>(2);
        header.put("typ","JWT");
        header.put("alg","HMAC512");

        String[] split = sub.toString().split("[\\(\\)]");
        String s = split[1];
        String[] split1 = s.split(",");
        String[] split2 = split1[0].split("=");
        String[] split3 = split1[1].split("=");

        return tokenPrefix + JWT.create()
                .withHeader(header)
                .withClaim("userId",split2[1])
                .withClaim("userTokenId",split3[1])
                .withSubject(sub.toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + expireTime))
                .sign(Algorithm.HMAC512(secret));
    }


    /**
     * 验证token
     * @param token
     */
    public static String validateToken(String token){
        try {
            return JWT.require(Algorithm.HMAC512(secret))
                    .build()
                    .verify(token.replace(tokenPrefix, ""))
                    .getSubject();
        } catch (TokenExpiredException e){
            throw new ApiException("token已经过期");
        } catch (Exception e){
            throw new ApiException("token验证失败");
            //throw new ApiException(ResultInfo.unauthorized("token验证失败"));
        }
    }

    /**
     * 检查token是否需要更新
     * @param token
     * @return
     */
    public static boolean isNeedUpdate(String token){
        //获取token过期时间
        Date expiresAt = null;
        try {
            expiresAt = JWT.require(Algorithm.HMAC512(secret))
                    .build()
                    .verify(token.replace(tokenPrefix, ""))
                    .getExpiresAt();
        } catch (TokenExpiredException e){
            return true;
        } catch (Exception e){
            throw new ApiException("token验证失败");
        }
        //如果剩余过期时间少于过期时常的一般时 需要更新
        return (expiresAt.getTime()-System.currentTimeMillis()) < (expireTime>>1);
    }

}


