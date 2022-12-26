package com.dasd412.api.diaryservice.adapter.out.persistence.food;

import java.util.List;

public interface FoodRepositoryCustom {

    void deleteFoodsInIds(List<Long> foodIds);
}
