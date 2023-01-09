package com.dasd412.api.diaryservice.adapter.out.persistence.diet;

import com.dasd412.api.diaryservice.domain.diet.Diet;

import java.util.List;
import java.util.concurrent.TimeoutException;

public interface DietRepositoryCustom {
    List<Diet> findDietsInDiary(Long diaryId)throws TimeoutException;
    void deleteDietsInIds(List<Long> dietIds)throws TimeoutException;
    void deleteAllOfWriter(Long writerId);
}
