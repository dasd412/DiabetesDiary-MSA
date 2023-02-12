package com.dasd412.api.readdiaryservice.application.service;

import com.dasd412.api.readdiaryservice.adapter.in.msessage.model.diary.dto.DiaryToReaderDTO;
import com.dasd412.api.readdiaryservice.domain.diary.DiabetesDiaryDocument;

public interface DiaryDataSyncService {
    void createDocument(DiaryToReaderDTO diaryToReaderDTO);
    void updateDocument(DiabetesDiaryDocument targetDiary,DiaryToReaderDTO diaryToReaderDTO);
    void deleteDocument(DiabetesDiaryDocument targetDiary);

    void deleteAllOfWriter(Long writerId);
}
