package com.star.config;

import com.star.Interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * API拦截器配置
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Value("${my-server.api-context-path}")
    private String API;
    
    @Bean
    public JwtInterceptor jwtInterceptor() {
        return new JwtInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截器注册
        registry.addInterceptor(jwtInterceptor())
                .addPathPatterns("/**")
                // 放行登录、注册请求以及Knife4j相关路径
                .excludePathPatterns(
                        API + "/user/login",
                        API + "/user/register",
                        API + "/file/upload",
                        API + "/file/getFile",
                        // Knife4j相关路径
                        API + "/doc.html",
                        API + "/webjars/**",
                        API + "/swagger-resources/**",
                        API + "/v2/api-docs/**",
                        API + "/v3/api-docs/**",
                        API + "/favicon.ico"
                );
    }
}
