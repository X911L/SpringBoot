package com.xl.pojo;

import lombok.Data;
import org.apache.ibatis.annotations.ConstructorArgs;

/**
 * @author xl
 * @date 2021年10月26日 14:23
 */
@Data
public class User {

    private Long id;

    private String phone;

    private String password;

    private String userId;

}
