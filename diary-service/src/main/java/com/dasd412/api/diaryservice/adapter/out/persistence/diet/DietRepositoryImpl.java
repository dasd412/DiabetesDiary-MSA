package com.dasd412.api.diaryservice.adapter.out.persistence.diet;


import com.dasd412.api.diaryservice.domain.diary.QDiabetesDiary;
import com.dasd412.api.diaryservice.domain.diet.Diet;
import com.dasd412.api.diaryservice.domain.diet.QDiet;
import com.dasd412.api.diaryservice.domain.food.QFood;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class DietRepositoryImpl implements DietRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public DietRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Diet> findDietsInDiary(Long diaryId) {
        return jpaQueryFactory.selectFrom(QDiet.diet)
                .innerJoin(QDiet.diet.diary, QDiabetesDiary.diabetesDiary)
                .innerJoin(QDiet.diet.foodList, QFood.food)
                .fetchJoin()
                .where(QDiet.diet.diary.diaryId.eq(diaryId).and(QFood.food.diet.dietId.eq(QDiet.diet.dietId)))
                .fetch().stream().distinct().collect(Collectors.toList());
    }

    @Override
    public void deleteDietsInIds(List<Long> dietIds) {
        jpaQueryFactory.delete(QDiet.diet)
                .where(QDiet.diet.dietId.in(dietIds))
                .execute();
    }

    @Override
    public void deleteAllOfWriter(Long writerId) {
        jpaQueryFactory.delete(QDiet.diet)
                .where(QDiet.diet.writerId.eq(writerId))
                .execute();
    }
}
