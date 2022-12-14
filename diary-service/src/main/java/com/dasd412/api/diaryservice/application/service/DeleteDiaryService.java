package com.dasd412.api.diaryservice.application.service;

import java.util.concurrent.TimeoutException;

public interface DeleteDiaryService extends DiaryService{

    Long deleteDiaryWithSubEntities(Long diaryId,Long writerId)throws TimeoutException;

    void deleteAllOfWriter(Long writerId);
}
