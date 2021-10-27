package com.xl.common;/**
 * @Author: xl
 * @Date: 2021-08-30 15:18
 * @Description:
 */

/**
 * @author xl
 * @date 2021年08月30日 15:18
 */
public enum CommonErrorCode implements ErrorCode {


    E_10001(10001, "无此用户"),
    E_10002(10002, "密码错误"),
    E_10003(10003, "请求参数错误"),
    E_10004(10004, "Authorization和USER_LOGIN_TOKEN传入不一致"),
    E_10005(10005, "antibrush不能为空"),
    E_10006(10006, "请求参数加密失败"),

    /**
     * 未知错误
     */
    UNKOWN(999999, "未知错误"),

    SUCCESS(0, "成功"),

    ;


    private int code;
    private String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    private CommonErrorCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
