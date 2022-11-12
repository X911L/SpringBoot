package com.xl.common.config;

import com.xl.common.util.AntiBrushFilter;
import com.xl.common.util.UserLoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;

@Configuration
//@ComponentScan
@ComponentScan(basePackages = "com.xl.common") //全局异常处理类需要被扫描才能
public class WebInterceptorConfig implements WebMvcConfigurer {
    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor(new UserLoginInterceptor())
        //        //.addPathPatterns("/userInfo/**")
        //        //.addPathPatterns("/user/login")
        //        .excludePathPatterns("/user/registry");//开放注册路径
    }

    @Bean
    public Filter antiBrushFilter() {
        return new AntiBrushFilter();
    }

    //  跨域第一种解决方法
    // 实现WebMvcConfigurer#addCorsMappings的方法
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600)
                .allowedHeaders("*");
    }

}



