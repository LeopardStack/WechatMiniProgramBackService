package com.scnu.wechatminiprogrambackservice.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "api")
@Data
public class ApiKeyConfig {
    private Map<String, String> keys = new HashMap<>();

    @PostConstruct
    public void init() {
        log.info("已加载API密钥配置: " + keys);
    }
}