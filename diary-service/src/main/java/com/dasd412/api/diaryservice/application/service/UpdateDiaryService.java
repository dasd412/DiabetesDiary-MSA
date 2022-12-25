package com.dasd412.api.diaryservice.application.service;

import com.dasd412.api.diaryservice.adapter.in.web.dto.update.DiaryUpdateRequestDTO;

import java.util.concurrent.TimeoutException;

public interface UpdateDiaryService extends DiaryService {

    Long updateDiaryWithEntities(DiaryUpdateRequestDTO dto) throws TimeoutException;
}
