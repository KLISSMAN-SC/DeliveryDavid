package com.proyecto.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncMailConfig {
  @Bean("mailExecutor")
  public Executor mailExecutor() {
    var e = new ThreadPoolTaskExecutor();
    e.setCorePoolSize(2);
    e.setMaxPoolSize(2);
    e.setQueueCapacity(50);
    e.setThreadNamePrefix("account-mail-");
    e.initialize();
    return e;
  }
}
