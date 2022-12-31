package com.dasd412.api.writerservice.adapter.out.persistence.writer_authority;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class WriterAuthorityRepositoryImpl implements WriterAuthorityRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public WriterAuthorityRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }
}
