package com.dasd412.api.writerservice.adapter.out.persistence.authority;

import com.dasd412.api.writerservice.domain.authority.Authority;
import com.dasd412.api.writerservice.domain.authority.QAuthority;
import com.dasd412.api.writerservice.domain.authority.QWriterAuthority;
import com.dasd412.api.writerservice.domain.writer.QWriter;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

public class AuthorityRepositoryImpl implements AuthorityRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public AuthorityRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Authority> findAllAuthority(long writerId) {
        return jpaQueryFactory.select(QAuthority.authority)
                .from(QAuthority.authority)
                .join(QAuthority.authority.writerAuthorities, QWriterAuthority.writerAuthority)
                .join(QWriterAuthority.writerAuthority.writer, QWriter.writer)
                .where(QWriter.writer.writerId.eq(writerId))
                .fetch();
    }

    @Override
    public void deleteAuthorityInIds(List<Long> ids) {
        jpaQueryFactory.delete(QAuthority.authority)
                .where(QAuthority.authority.id.in(ids))
                .execute();
    }
}
