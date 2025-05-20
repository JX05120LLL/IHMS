package com.star.service.ai;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * AI服务配置类
 * 用于配置AI服务相关的Bean和属性
 */
@Configuration
public class AIServiceConfig {

    /**
     * 创建RestTemplate用于与外部AI服务通信
     * @return RestTemplate实例
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
} 