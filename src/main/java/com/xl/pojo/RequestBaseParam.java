package com.xl.pojo;

import lombok.Data;

/**
 * @author X
 * @date 2022年11月12日 12:50
 */
@Data
public class RequestBaseParam {

    private static final long serialVersionUID = -5982005772086117214L;
    private String clientUserAgent;
    private String clientIp;
    private String serialId;
    private Double longitude;
    private Double latitude;
    private String loginName;

}
