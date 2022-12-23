package com.dasd412.api.diaryservice.adapter.out.persistence.food;

import com.dasd412.api.diaryservice.adapter.out.persistence.food.FoodRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class FoodRepositoryImpl implements FoodRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public FoodRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

}
