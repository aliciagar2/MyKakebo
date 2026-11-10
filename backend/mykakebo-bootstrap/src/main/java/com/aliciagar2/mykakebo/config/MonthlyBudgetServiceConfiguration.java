package com.aliciagar2.mykakebo.config;

import com.aliciagar2.mykakebo.application.MonthlyBudgetService;
import com.aliciagar2.mykakebo.application.port.MonthlyBudgetRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonthlyBudgetServiceConfiguration {

    @Bean
    public MonthlyBudgetService monthlyBudgetService(MonthlyBudgetRepositoryPort monthlyBudgetRepositoryPort) {
        return new MonthlyBudgetService(monthlyBudgetRepositoryPort);
    }
}
