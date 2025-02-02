package com.mercadolivre.tracker_logistic.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    private final DatabaseProperties hikariProperties;

    public DatabaseConfig(DatabaseProperties hikariProperties) {
        this.hikariProperties = hikariProperties;
    }

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(databaseUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setMaximumPoolSize(hikariProperties.getMaximumPoolSize());
        dataSource.setMinimumIdle(hikariProperties.getMinimumIdle());
        dataSource.setConnectionTimeout(hikariProperties.getConnectionTimeout());
        dataSource.setIdleTimeout(hikariProperties.getIdleTimeout());
        dataSource.setMaxLifetime(hikariProperties.getMaxLifetime());
        dataSource.setKeepaliveTime(hikariProperties.getKeepaliveTime());
        dataSource.setValidationTimeout(hikariProperties.getValidationTimeout());
        return dataSource;
    }
}
