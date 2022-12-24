package com.dasd412.api.diaryservice.adapter.out.persistence.diet;

import com.dasd412.api.diaryservice.adapter.out.persistence.diet.DietRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class DietRepositoryImpl implements DietRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public DietRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

}
