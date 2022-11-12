package com.xl.pojo;

import java.util.Arrays;
import java.util.List;

/**
 * @author X
 * @date 2022年11月12日 12:49
 */
public class Constants {

    public static final String TY_OPENID = "ty";
    public static final String EMPTY_IP = "0.0.0.0";
    public static final String AUTHORIZATION = "Authorization";
    public static final String APP_ID = "appId";
    public static final String DATE = "Date";
    public static final String DATD = "Data";
    public static final String SIGNATURE = "Signature";
    public static final String TIMESTAMP = "Timestamp";
    public static final String NONCE = "Nonce";
    public static final String SOURCE = "Source";
    public static final String BEARER = "Bearer ";
    public static final String SCRET = "scret";
    public static final String EDITION = "edition";
    public static final String TENCENT_BSP = "tencent:bsp:";
    public static final String MF_ACCOUNT_TYPE = "mf:2:";
    public static final String VALIDATE_KEY = "validate";
    public static final String USER_AGENT = "user-agent";
    public static final String CLIENT_IP = "clientIp";
    public static final String SERIAL_ID = "serialId";
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";
    public static final int RETRY_CNT = 3;
    public static final String CLAIM_DEFAULT_ERROR = "no_result";
    public static final String MINI_LOGIN_PATH = "/mp/login";
    public static final String WECHAT_LOGIN_PATH = "/wp/login";
    public static final String THIRD_LOGIN_PATH = "/th/login";
    public static final String INNER_LOGIN_PATH = "/inner/login";
    public static final String ESE_LOGIN_PATH = "/ese/login";
    public static final String PLUGIN_LOGIN_PATH = "/plugin/login";
    public static final String AUTH_EXCHANGE_PATH = "/auth/exchange";
    public static final String EXCHANGE_PATH = "/exchange";
    public static final String APIT_PATH = "/apit";
    public static final String APIY_PATH = "/apiy";
    public static final int READ_TIME_OUT = 4000;
    public static final int CONNECT_TIME_OUT = 1000;
    public static final String DATA_KEY = "data";
    public static final int PRIZE_COUNT_HASH_SIZE = 2;
    public static final String EXCEPTION_DATA = "exception:data";
    public static final String LOG_FROM = "openapi:";
    public static final String LOG_DATA = "openapi:log";
    public static final String WECHAT_CODE_PREIX = "wxticket";
    public static final String SECURITY_PROPERTIES_KEY = "security:pro";
    public static final String CLAIM_CHANGE_SCRMERROR = "openapi:claim:change:scrmerror";
    public static final String CLAIM_SYSTEM_FAIL = "openapi:claim:system:fail";
    public static final String TAG_LOTTERY_USER_PUSH_EXCEPTION_DATA = "exception:data:lottery:tagUser";
    public static final String POINT_WITHDRAWAL_SCRMERROR = "openapi:point:withdrawal:scrmerror";
    public static final String CLAIM_EXCEPTION_DATA = "exception:data:claim";
    public static final String POINT_WITHDRAWAL_EXCEPTION_DATA = "exception:data:pointWithdrawal";
    public static final String CLAIM_BEFORE_EXCEPTION_DATA = "exception:data:beforeClaim";
    public static final String TAG_LOTTERY_PUSH_EXCEPTION_DATA = "exception:data:lottery:tag";
    public static final String INTERACT_LOTTERY_PUSH_EXCEPTION_DATA = "exception:data:lottery:interact";
    public static final String CARD_UPDATE_EXCEPTION_DATA = "exception:data:updateCard";
    public static final List<String> EXCEPTION_LOG_LIST = Arrays.asList("exception:data:claim", "exception:data:lottery:tag", "exception:data:lottery:interact", "exception:data:beforeClaim", "exception:data:pointWithdrawal", "openapi:point:sync:error", "exception:data:updateCard");
    public static final String TRUE_STR = "true";
    public static final String FALSE_STR = "false";
    public static final String REDIS_OPEN_API_SELF_KEY = "KafkaLogs:log";
    public static final String COUNTER_EXPIRE_PREFIX = "ex:";
    public static final String COLON = ":";
    public static final String UNDERLINE = "_";
    public static final String ANTIBRUSH = "AntiBrush";
    public static final String SEQNUM = "SEQNUM";
    public static final String ESESEQNUM = "ESESEQNUM";
    public static final String SIGN = "sign";
    public static final String SECRET_ID = "secretId";
    public static final String TM_ACCOUNT_KEY = "tmAccount:";
    public static final String TM_LOGIN_ACCOUNT_KEY = "tmLoginAccount:";
    public static final String TM_PROMOTION_CODE_ACCOUNT_KEY = "tmPromotionCodeAccount:";

    public Constants() {
    }

}
