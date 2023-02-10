package com.dasd412.api.diaryservice.application.service;

import com.dasd412.api.diaryservice.adapter.out.message.model.readdiary.dto.DiaryToReaderDTO;

import java.util.concurrent.TimeoutException;

public interface DeleteDiaryService extends DiaryService{

    DiaryToReaderDTO deleteDiaryWithSubEntities(Long diaryId, Long writerId)throws TimeoutException;
    void sendMessageToWriterService(Long writerId, Long diaryId) throws TimeoutException;
    void sendMessageToFindDiaryService(DiaryToReaderDTO dto)throws TimeoutException;
    void deleteAllOfWriter(Long writerId);
}
