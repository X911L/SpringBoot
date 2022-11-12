package com.xl.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class JwtHeader implements Serializable {

    private static final long serialVersionUID = -1473437865260147295L;

    private String type;

    private String alg;

    private Integer kid;
}