package com.mercadolivre.tracker_logistic.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.datasource.hikari")
public class DatabaseProperties {
    private int maximumPoolSize;
    private int minimumIdle;
    private int connectionTimeout;
    private int idleTimeout;
    private int maxLifetime;
    private int keepaliveTime;
    private int validationTimeout;
}
