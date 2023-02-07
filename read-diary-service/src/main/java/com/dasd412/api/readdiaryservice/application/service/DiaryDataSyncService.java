package com.dasd412.api.readdiaryservice.application.service;

import com.dasd412.api.readdiaryservice.adapter.in.msessage.model.diary.dto.DiaryToReaderDTO;

public interface DiaryDataSyncService {

    void createDocument(DiaryToReaderDTO diaryToReaderDTO);
}
