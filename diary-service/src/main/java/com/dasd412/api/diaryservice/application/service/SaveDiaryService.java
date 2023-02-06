package com.dasd412.api.diaryservice.application.service;

import com.dasd412.api.diaryservice.adapter.in.web.dto.post.DiaryPostRequestDTO;

import java.util.concurrent.TimeoutException;

public interface SaveDiaryService extends DiaryService{
    Long postDiaryWithEntities(Long writerId, DiaryPostRequestDTO dto) throws TimeoutException;

    void sendMessageToWriterService(Long writerId, Long diaryId) throws TimeoutException;
    void sendMessageToFindDiaryService();
}
