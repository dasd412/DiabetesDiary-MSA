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

    @Override
    public void deleteWriterById(Long writerId) {
        jpaQueryFactory.delete(QWriter.writer)
                .where(QWriter.writer.writerId.eq(writerId))
                .execute();
    }

    /*
     * querydsl 에선 exists 사용시 count()를 사용하므로 총 몇건인 지 확인하기 위해 전체를 확인하는 추가 작업이 필요하다.
     * 따라서 Querydsl 이 기본적으로 제공하는 exists 는 성능 상 좋지 않다.
     * 대신 fetchFirst()를 사용하여 limit(1)의 효과를 낼 수 있도록 하면 성능이 개선된다.
     */
    @Override
    public Boolean existName(String username) {
        Integer fetchFirst = jpaQueryFactory
                .selectOne()
                .from(QWriter.writer)
                .where(QWriter.writer.name.eq(username))
                .fetchFirst();/* 값이 없으면 0이 아니라 null 반환. */

        return fetchFirst != null;
    }

    @Override
    public Boolean existEmail(String email, String provider) {
        Integer fetchFirst;
        if (provider == null) {
            fetchFirst = jpaQueryFactory
                    .selectOne()
                    .from(QWriter.writer)
                    .where(QWriter.writer.email.eq(email))
                    .fetchFirst();
        } else {
            /* 이메일이 같더라도 provider 가 다르면 다른 걸로 인식하게 하기 */
            fetchFirst = jpaQueryFactory
                    .selectOne()
                    .from(QWriter.writer)
                    .where(QWriter.writer.email.eq(email).and(QWriter.writer.provider.eq(provider)))
                    .fetchFirst();
        }
        return fetchFirst != null;
    }
}
