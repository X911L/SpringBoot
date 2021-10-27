package com.xl.pojo;

import lombok.Data;

/**
 * @author xl
 * @date 2021年10月26日 15:11
 */
@Data
public class Token extends BaseToken {

    private Long userId;

    private Long userTokenId;

}
