package com.aliciagar2.mykakebo.config;

import com.aliciagar2.mykakebo.application.ExpenseService;
import com.aliciagar2.mykakebo.application.port.ExpenseRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExpenseServiceConfiguration {

    @Bean
    public ExpenseService expenseService(ExpenseRepositoryPort expenseRepositoryPort) {
        return new ExpenseService(expenseRepositoryPort);
    }
}
