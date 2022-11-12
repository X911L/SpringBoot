package com.xl.common.bean;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "xl")
public class DigestProperties {

    private String openApiAesIv;

    private String openApiAesKey;

    private List<String> openApiKeys;
}
