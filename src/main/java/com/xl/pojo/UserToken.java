package com.xl.pojo;

import lombok.Data;

/**
 * @author xl
 * @date 2021年10月26日 18:30
 */
@Data
public class UserToken {

    //用户id
    private Long id;

    private String token;

    private String userId;

}
