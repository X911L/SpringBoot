package com.xl.common.config;

import com.xl.common.AntiBrushFilter;
import com.xl.common.UserLoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;

@Configuration
@ComponentScan(basePackages = "com.xl.common") //全局异常处理类需要被扫描才能
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserLoginInterceptor())
                .addPathPatterns("/user/**")
                .addPathPatterns("/userInfo/**")
                .addPathPatterns("/user/login")
                .excludePathPatterns("/user/registry");//开放注册路径
    }

    @Bean
    public Filter antiBrushFilter() {
        return new AntiBrushFilter();
    }

}



