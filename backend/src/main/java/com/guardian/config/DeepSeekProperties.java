package com.guardian.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "guardian.deepseek")
public class DeepSeekProperties {

    private String apiUrl;
    private String apiKey;
    private String model;
}
