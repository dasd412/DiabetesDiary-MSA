package com.dasd412.api.diaryservice.adapter.out.persistence.diary;

import java.util.concurrent.TimeoutException;

public interface DiaryRepositoryCustom {

    void deleteDiaryForBulkDelete(Long diaryId)throws TimeoutException;
    void deleteAllOfWriter(Long writerId);
}
