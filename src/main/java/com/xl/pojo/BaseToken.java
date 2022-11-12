package com.xl.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xl
 * @date 2021年10月26日 18:26
 */
@Data
public abstract class BaseToken implements Serializable {

    private static final long serialVersionUID = -7302865694002611903L;
    /**
     * "exp" (Expiration Time)指的是过期时间，假如超过过期时间，则会抛出异常
     */
    private Integer exp;
    //private Integer exp;

    /**
     * "nbf" (Not Before) Claim指的是开始日期，claim要求当前日期/时间必须在以后或等于
     * 在“nbf”声明中列出的日期/时间
     */
    private Integer nbf;

    /**
     * "iss" (issuer)是签发该证书的负责人。
     */
    private String iss;

    /**
     * "sub" (Subject)是主体
     */
    private String sub;

    /**
     * "aud" (Audience) Claim是指jwt的接受者，假如aud没有发现，则解析jwt时会抛出异常
     */
    private String aud;

    /**
     * "iat" (Issued At) Claim是指jwt的发行时间
     */
    private Integer iat;

    /**
     * "jti" (JWT ID) Claim为JWT提供了惟一的标识符，如果应用程序
     * 使用多个发行者，必须在值之间避免冲突，
     * 由不同的发行商制作
     */
    private String jti;
}
