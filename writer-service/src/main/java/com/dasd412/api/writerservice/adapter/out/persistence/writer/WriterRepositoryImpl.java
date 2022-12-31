package com.dasd412.api.writerservice.adapter.out.persistence.writer;

import com.dasd412.api.writerservice.domain.writer.QWriter;
import com.dasd412.api.writerservice.domain.writer.Writer;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.Optional;

public class WriterRepositoryImpl implements WriterRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public WriterRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Optional<Writer> findWriterByName(String username) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(QWriter.writer)
                .where(QWriter.writer.name.eq(username))
                .fetchOne());
    }
}
