package com.mercadolivre.tracker_logistic.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    private final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    private static final Logger logger = LoggerFactory.getLogger(AsyncConfig.class);

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor(AsyncProperties asyncProperties) {
        executor.setCorePoolSize(asyncProperties.getCorePoolSize());
        executor.setMaxPoolSize(asyncProperties.getMaxPoolSize());
        executor.setQueueCapacity(asyncProperties.getQueueCapacity());
        executor.setKeepAliveSeconds(asyncProperties.getKeepAliveSeconds());
        executor.setThreadNamePrefix("EventProcessor-");

        executor.setRejectedExecutionHandler((r, executor) -> {
            logger.error("Async Task Rejected: A fila de execução está cheia!");
            throw new RuntimeException("A fila de execução assíncrona está cheia e o seu dado não foi processado. Aguarde para enviar novamente.");
        });

        executor.initialize();
        return executor;
    }
}
