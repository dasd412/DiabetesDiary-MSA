package com.dasd412.api.diaryservice.domain.food;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class FoodRepositoryImpl implements FoodRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public FoodRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

}
