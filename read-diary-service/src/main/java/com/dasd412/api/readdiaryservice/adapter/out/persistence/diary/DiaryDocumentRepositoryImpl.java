package com.dasd412.api.readdiaryservice.adapter.out.persistence.diary;

import com.dasd412.api.readdiaryservice.domain.diary.DiabetesDiaryDocument;
import com.dasd412.api.readdiaryservice.domain.diary.QDiabetesDiaryDocument;

import java.util.List;

public class DiaryDocumentRepositoryImpl implements DiaryDocumentRepositoryCustom{

    @Override
    public List<DiabetesDiaryDocument> getDiabetesDiariesOfWriter(String writerId) {
        QDiabetesDiaryDocument diabetesDiaryDocument;
        return null;
    }
}
