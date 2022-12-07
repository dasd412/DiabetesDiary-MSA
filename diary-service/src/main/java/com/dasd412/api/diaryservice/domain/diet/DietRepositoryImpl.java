package com.dasd412.api.diaryservice.domain.diet;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class DietRepositoryImpl implements DietRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public DietRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }
    @Override
    public Long findMaxOfId() {
        return jpaQueryFactory.from(QDiet.diet).select(QDiet.diet.dietId.max()).fetchOne();
    }
}
