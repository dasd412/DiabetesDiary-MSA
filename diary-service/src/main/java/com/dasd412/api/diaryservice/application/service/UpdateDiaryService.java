package com.dasd412.api.diaryservice.application.service;

import com.dasd412.api.diaryservice.adapter.in.web.dto.update.DiaryUpdateRequestDTO;
import com.dasd412.api.diaryservice.adapter.out.message.model.readdiary.dto.DiaryToReaderDTO;

import java.util.concurrent.TimeoutException;

public interface UpdateDiaryService extends DiaryService {

    DiaryToReaderDTO updateDiaryWithEntities(Long writerId, DiaryUpdateRequestDTO dto) throws TimeoutException;

    void sendMessageToFindDiaryService(DiaryToReaderDTO dto)throws TimeoutException;
}
