package com.dasd412.api.diaryservice.adapter.out.persistence.diet;

import com.dasd412.api.diaryservice.domain.diet.Diet;

import java.util.List;

public interface DietRepositoryCustom {
    List<Diet> findDietsInDiary(Long diaryId);
    void deleteDietsInIds(List<Long> dietIds);
}
