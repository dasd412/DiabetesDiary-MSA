package com.dasd412.api.diaryservice.domain.diary;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class DiaryRepositoryImpl implements DiaryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public DiaryRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

}
