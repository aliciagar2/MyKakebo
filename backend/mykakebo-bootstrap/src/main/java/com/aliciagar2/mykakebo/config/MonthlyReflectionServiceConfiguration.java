package com.aliciagar2.mykakebo.config;

import com.aliciagar2.mykakebo.application.MonthlyReflectionService;
import com.aliciagar2.mykakebo.application.port.MonthlyReflectionRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonthlyReflectionServiceConfiguration {

    @Bean
    public MonthlyReflectionService monthlyReflectionService(
            MonthlyReflectionRepositoryPort monthlyReflectionRepositoryPort) {
        return new MonthlyReflectionService(monthlyReflectionRepositoryPort);
    }
}
