package com.xl.common.util;

import com.xl.common.bean.DigestProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * KeyUtils
 *
 * @author User
 * @date 2018/02/07
 **/
@Configuration
@ConditionalOnProperty("xl.jwt.enable")
@EnableConfigurationProperties(DigestProperties.class)
@RequiredArgsConstructor
public class KeyUtils {

    private final static Map<Integer, String> DIGEST_KEY_MAP = new HashMap<>();

    public static String aesKey;

    public static String aesIv;

    private final DigestProperties digestProperties;

    /**
     * 通过企业id（必传）获取key索引，因0给gateway_open用，1给openapi_open用，若取到0,1返2
     *
     * @param eseId 企业id
     * @return 索引
     */
    public static int getKeyIndex(Long eseId) {
        int index = (int) (eseId % 100);
        if (index == 0 || index == 1) {
            return 2;
        }
        return index;
    }

    @PostConstruct
    public void initDigestKeyMap() {
        KeyUtils.aesKey = digestProperties.getOpenApiAesKey();
        KeyUtils.aesIv = digestProperties.getOpenApiAesIv();
        List<String> keys = digestProperties.getOpenApiKeys();
        for (int i = 0; i < keys.size(); i++) {
            DIGEST_KEY_MAP.put(i, keys.get(i));
        }
    }

    /**
     * 随机获取一个key
     *
     * @return key key
     */
    public static int randomDigestKeyIndex() {
        return ThreadLocalRandom.current().nextInt(100);
    }

    /**
     * 获取index 指定的key
     *
     * @param index index
     * @return key key
     */
    public static String getSpecifiedKey(int index) {
        return DIGEST_KEY_MAP.get(index);
    }


    /**
     * 获取index 指定的base64编码后的key
     *
     * @param index index
     * @return key key
     */
    public static String getBase64SecurityKey(int index) {
        return DIGEST_KEY_MAP.get(index);
    }


}
