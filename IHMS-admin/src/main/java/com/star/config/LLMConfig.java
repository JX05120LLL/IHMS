package com.star.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 大模型配置类
 */
@Configuration
public class LLMConfig {
    
    @Value("${llm.connect-timeout:10000}")
    private int connectTimeout;
    
    @Value("${llm.read-timeout:30000}")
    private int readTimeout;
    
    /**
     * RestTemplate配置，用于调用大模型API
     */
    @Bean
    public RestTemplate llmRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        
        return new RestTemplate(factory);
    }
} 