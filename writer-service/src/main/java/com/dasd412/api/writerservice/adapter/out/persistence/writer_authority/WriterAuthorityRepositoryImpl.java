package com.dasd412.api.writerservice.adapter.out.persistence.writer_authority;

import com.dasd412.api.writerservice.domain.authority.QAuthority;
import com.dasd412.api.writerservice.domain.authority.QWriterAuthority;
import com.dasd412.api.writerservice.domain.authority.WriterAuthority;
import com.dasd412.api.writerservice.domain.writer.QWriter;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

public class WriterAuthorityRepositoryImpl implements WriterAuthorityRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public WriterAuthorityRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<WriterAuthority> findAllWriterAuthority(Long writerId) {
        return jpaQueryFactory.select(QWriterAuthority.writerAuthority)
                .from(QWriterAuthority.writerAuthority)
                .join(QWriterAuthority.writerAuthority.authority, QAuthority.authority)
                .join(QWriterAuthority.writerAuthority.writer, QWriter.writer)
                .where(QWriter.writer.writerId.eq(writerId))
                .fetch();
    }

    @Override
    public void deleteWriterAuthorityInIds(List<Long> ids) {
        jpaQueryFactory.delete(QWriterAuthority.writerAuthority)
                .where(QWriterAuthority.writerAuthority.id.in(ids))
                .execute();
    }
}
