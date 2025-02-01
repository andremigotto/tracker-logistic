package com.mercadolivre.tracker_logistic.config;

import com.mercadolivre.tracker_logistic.exception.AsyncExecutionException;
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
    public Executor asyncExecutor() {
        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(200);
        executor.setQueueCapacity(5000);
        executor.setKeepAliveSeconds(30);
        executor.setThreadNamePrefix("EventProcessor-");

        executor.setRejectedExecutionHandler((r, executor) -> {
            logger.error("🚨 Async Task Rejected: A fila de execução está cheia!");
            throw new RuntimeException("A fila de execução assíncrona está cheia. Tente novamente mais tarde.");
        });

        executor.initialize();
        return executor;
    }
}
