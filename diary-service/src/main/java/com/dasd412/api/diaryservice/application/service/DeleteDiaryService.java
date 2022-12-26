package com.dasd412.api.diaryservice.application.service;

import com.dasd412.api.diaryservice.adapter.in.web.dto.delete.DiaryDeleteRequestDTO;

import java.util.concurrent.TimeoutException;

public interface DeleteDiaryService extends DiaryService{

    Long deleteDiaryWithSubEntities(DiaryDeleteRequestDTO dto)throws TimeoutException;
}
