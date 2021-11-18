package com.xl.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 解决跨域方法三
 * @author xl
 * @date 2021年11月17日 17:12
 */
@Slf4j
@Component
@WebFilter(urlPatterns = {"/*"}, filterName = "headerFilter")
public class HeaderFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) resp;
        //解决跨域访问报错
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        //设置过期时间
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, client_id, uuid, Authorization");
        // 支持HTTP 1.1.
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        // 支持HTTP 1.0. response.setHeader("Expires", "0");
        response.setHeader("Pragma", "no-cache");
        // 编码
        response.setCharacterEncoding("UTF-8");
        chain.doFilter(request, resp);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("跨域过滤器启动");
    }

    @Override
    public void destroy() {
        log.info("跨域过滤器销毁");
    }

}

// 解决跨域方法四  @CrossOrigin
// 创建一个filter解决跨域


