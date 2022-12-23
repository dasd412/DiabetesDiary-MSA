package com.dasd412.api.diaryservice.adapter.out.persistence.diary;

import com.dasd412.api.diaryservice.adapter.out.persistence.diary.DiaryRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class DiaryRepositoryImpl implements DiaryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public DiaryRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

}
