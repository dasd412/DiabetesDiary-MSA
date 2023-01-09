package com.dasd412.api.diaryservice.adapter.out.persistence.food;

import com.dasd412.api.diaryservice.domain.food.QFood;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

@SuppressWarnings("unused")
public class FoodRepositoryImpl implements FoodRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public FoodRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public void deleteFoodsInIds(List<Long> foodIds) {
        jpaQueryFactory.delete(QFood.food)
                .where(QFood.food.foodId.in(foodIds))
                .execute();
    }

    @Override
    public void deleteAllOfWriter(Long writerId) {
        jpaQueryFactory.delete(QFood.food)
                .where(QFood.food.writerId.eq(writerId))
                .execute();
    }
}
