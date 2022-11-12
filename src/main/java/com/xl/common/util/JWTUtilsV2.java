package com.xl.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xl.pojo.BaseToken;
import com.xl.pojo.JwtHeader;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author X
 * @date 2022年11月08日 14:31
 */
public class JWTUtilsV2 {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 获取openApi token 默认有效期1小时
     *
     * @return Token
     */
    public static <T extends BaseToken> String getToken(T token, Long eseId) {
        if (eseId == null) {
            throw new RuntimeException("eseId cannot be null");
        }
        return JWTUtilsV2.createToken(token, eseId);
    }

    /**
     * 获取top平台网关token 默认有效期5分钟
     *
     * @return gatewayToken
     */
    //public static String getGatewayToken() {
    //    GatewayToken gatewayToken = new GatewayToken();
    //    gatewayToken.setNonce(UUID.randomUUID().toString());
    //    gatewayToken.setTimestamp(System.currentTimeMillis());
    //    //网关token过期时间5分钟
    //    gatewayToken.setExp(300000);
    //    return JwtUtils.createToken(gatewayToken, null);
    //}

    /**
     * 获取码中台token 默认有效期5分钟
     *
     * @return getMztToken
     */
    //public static String getMztToken(String base64Security) {
    //    GatewayToken gatewayToken = new GatewayToken();
    //    gatewayToken.setNonce(UUID.randomUUID().toString());
    //    gatewayToken.setTimestamp(System.currentTimeMillis());
    //    //网关token过期时间5分钟
    //    gatewayToken.setExp(300000);
    //    return JwtUtils.createTokenByKey(gatewayToken, base64Security);
    //}

    /**
     * 获取top平台网关token 永久
     *
     * @return gatewayToken
     */
    //public static String getGatewayTokenNoExp() {
    //    GatewayToken gatewayToken = new GatewayToken();
    //    gatewayToken.setNonce(UUID.randomUUID().toString());
    //    gatewayToken.setTimestamp(System.currentTimeMillis());
    //    return JwtUtils.createToken(gatewayToken, null);
    //}
    private static <T extends BaseToken> String createToken(T token, Long eseId) {
        int kid = 0;
        if (eseId != null) {
            kid = KeyUtils.getKeyIndex(eseId);
        }
        return createTokenByKid(token, kid);
    }

    private static <T extends BaseToken> String createTokenByKid(T token, int kid) {
        if (token == null) {
            throw new RuntimeException("token cannot be null");
        }
        Integer exp = token.getExp();
        Map<String, Object> headerMap = new HashMap<>(5);
        headerMap.put("type", "JWT");
        headerMap.put("alg", "HS256");
        headerMap.put("kid", kid);

        //Map claimMap = new BeanMap(token);
        Map claimMap = transBean2Map(token);
        String base64Security = KeyUtils.getBase64SecurityKey(kid);

        if (exp == null) {
            //token 默认有效时间是1小时
            exp = 3600000;
        }
        return createJWT(headerMap, claimMap, base64Security, exp, token.getAud(), token.getIss());
    }

    public static <T extends BaseToken> String createTokenByKey(T token, String base64Security) {
        if (token == null) {
            throw new RuntimeException("token cannot be null");
        }
        Integer exp = token.getExp();


        Map<String, Object> headerMap = new HashMap<>(5);
        headerMap.put("type", "JWT");
        headerMap.put("alg", "HS256");

        //Map claimMap = new BeanMap(token);
        Map claimMap = transBean2Map(token);
        if (exp == null) {
            //token 默认有效时间是1小时
            exp = 3600000;
        }
        return createJWT(headerMap, claimMap, base64Security, exp, token.getAud(), token.getIss());
    }


    public static <T> T parseTokenByKey(String token, String base64Security,  Class<T> clazz) throws Exception {
        if (StringUtils.isEmpty(token)) {
            throw new RuntimeException("token cannot be null");
        }
        Claims claims = parseJWT(token, base64Security);
        if (claims != null) {
            String s = MAPPER.writeValueAsString(claims);
            return MAPPER.readValue(s, clazz);
        }
        return null;
    }



    public static <T> T parseToken(String token, Class<T> clazz) throws Exception {
        if (StringUtils.isEmpty(token)) {
            throw new RuntimeException("token cannot be null");
        }
        Integer kid = parseHeader(token).getKid();
        if (kid == null) {
            kid = 0;
        }
        String base64Security = KeyUtils.getBase64SecurityKey(kid);
        Claims claims = parseJWT(token, base64Security);

        if (claims != null) {
            String s = MAPPER.writeValueAsString(claims);
            return MAPPER.readValue(s, clazz);
//            BeanUtils.populate(obj, claims);
        }
        return null;
    }


    private static JwtHeader parseHeader(String jsonWebToken) throws IOException {
        String[] params = StringUtils.split(jsonWebToken, ".");
        String header = new String(Base64.decodeBase64(params[0]));
        return MAPPER.readValue(header, JwtHeader.class);
    }

    public static Claims parseJWT(String jsonWebToken, String base64Security) {
        return Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(base64Security))
                .parseClaimsJws(jsonWebToken).getBody();
    }

    private static String createJWT(Map headerMap, Map claimMap, String base64Security, Integer exp, String
            audience, String issuer) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        // 生成签名密钥
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(base64Security);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        // 添加构成JWT的参数
        JwtBuilder builder = Jwts.builder().setHeaderParams(headerMap).setClaims(claimMap).setAudience(audience).setIssuer(issuer)
                .signWith(signatureAlgorithm, signingKey);

        // 添加Token过期时间
        Date expTime = DateUtils.addMilliseconds(new Date(), exp);
        builder.setExpiration(expTime).setNotBefore(DateUtils.addMinutes(new Date(), -1));

        // 生成JWT
        return builder.compact();
    }

    private static Map<String, Object> transBean2Map(Object obj) {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>(100);
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (!"class".equals(key)) {
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);
                    map.put(key, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

}
