package com.aliciagar2.mykakebo.adapter.persistence.jpa;

import com.aliciagar2.mykakebo.domain.MonthlyReflection;
import com.aliciagar2.mykakebo.domain.MonthlyReflectionEntity;
import org.springframework.stereotype.Component;

@Component
public class MonthlyReflectionEntityMapper {

    // MonthlyReflectionEntity has no user column, so user is left null.
    public MonthlyReflection toDomain(MonthlyReflectionEntity entity) {
        return new MonthlyReflection(
                entity.getId(),
                null,
                entity.getYearMonth(),
                entity.getMoneyHad(),
                entity.getMoneySaved(),
                entity.getMoneySpent(),
                entity.getImprovementNote());
    }

    public MonthlyReflectionEntity toEntity(MonthlyReflection reflection) {
        return new MonthlyReflectionEntity(
                reflection.id(),
                reflection.monthYear(),
                reflection.moneyHad(),
                reflection.moneySaved(),
                reflection.moneySpent(),
                reflection.improvementNote());
    }
}
