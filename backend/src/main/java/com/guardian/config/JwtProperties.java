package com.guardian.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "guardian.jwt")
public class JwtProperties {

    private String secret;
    private Long expirationMinutes = 120L;
}
