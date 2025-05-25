package com.star.service.ai;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
    @Primary
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    /**
     * 医疗咨询助手服务配置项
     */
    @Bean
    public MedicalAssistantConfig medicalAssistantConfig() {
        MedicalAssistantConfig config = new MedicalAssistantConfig();
        config.setEnabled(true);
        config.setMaxResponseTokens(2000);
        config.setDefaultTemperature(0.3f);
        return config;
    }
    
    /**
     * 医疗咨询助手配置类
     */
    public static class MedicalAssistantConfig {
        /**
         * 是否启用医疗咨询助手
         */
        private boolean enabled = true;
        
        /**
         * 最大响应token数
         */
        private int maxResponseTokens = 2000;
        
        /**
         * 默认温度参数
         */
        private float defaultTemperature = 0.3f;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getMaxResponseTokens() {
            return maxResponseTokens;
        }

        public void setMaxResponseTokens(int maxResponseTokens) {
            this.maxResponseTokens = maxResponseTokens;
        }

        public float getDefaultTemperature() {
            return defaultTemperature;
        }

        public void setDefaultTemperature(float defaultTemperature) {
            this.defaultTemperature = defaultTemperature;
        }
    }
} 