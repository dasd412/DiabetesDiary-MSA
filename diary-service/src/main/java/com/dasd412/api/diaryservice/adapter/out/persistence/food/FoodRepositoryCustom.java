package com.dasd412.api.diaryservice.adapter.out.persistence.food;

import java.util.List;
import java.util.concurrent.TimeoutException;

public interface FoodRepositoryCustom {

    void deleteFoodsInIds(List<Long> foodIds)throws TimeoutException;

    void deleteAllOfWriter(Long writerId);
}
