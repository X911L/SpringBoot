package com.xl.common.config;

import com.xl.common.AntiBrushFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * @author xl
 * @date 2021年10月27日 13:42
 */
@Configuration
public class WebFilterConfig {

    @Bean
    public FilterRegistrationBean<Filter> apiYAuthFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(AntiBrushFilter());
        registration.setName("antiBrushFilter");
        registration.addUrlPatterns("/user/login");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public Filter AntiBrushFilter() {
        return new AntiBrushFilter();
    }


}
