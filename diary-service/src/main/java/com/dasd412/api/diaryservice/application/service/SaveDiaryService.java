package com.dasd412.api.diaryservice.application.service;

import com.dasd412.api.diaryservice.adapter.in.web.dto.post.DiaryPostRequestDTO;
import com.dasd412.api.diaryservice.adapter.out.message.model.readdiary.dto.DiaryToReaderDTO;

import java.util.concurrent.TimeoutException;

public interface SaveDiaryService extends DiaryService {
    DiaryToReaderDTO postDiaryWithEntities(Long writerId, DiaryPostRequestDTO dto) throws TimeoutException;
    void sendMessageToWriterService(Long writerId, Long diaryId) throws TimeoutException;

    void sendMessageToFindDiaryService(DiaryToReaderDTO dto);
}
