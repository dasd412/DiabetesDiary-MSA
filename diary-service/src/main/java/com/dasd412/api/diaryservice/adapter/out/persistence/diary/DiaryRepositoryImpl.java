package com.dasd412.api.diaryservice.adapter.out.persistence.diary;

import com.dasd412.api.diaryservice.domain.diary.QDiabetesDiary;
import com.querydsl.jpa.impl.JPAQueryFactory;

@SuppressWarnings("unused")
public class DiaryRepositoryImpl implements DiaryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public DiaryRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public void deleteDiaryForBulkDelete(Long diaryId) {
        jpaQueryFactory.delete(QDiabetesDiary.diabetesDiary)
                .where(QDiabetesDiary.diabetesDiary.diaryId.eq(diaryId))
                .execute();
    }

    @Override
    public void deleteAllOfWriter(Long writerId) {
        jpaQueryFactory.delete(QDiabetesDiary.diabetesDiary)
                .where(QDiabetesDiary.diabetesDiary.writerId.eq(writerId))
                .execute();
    }
}
