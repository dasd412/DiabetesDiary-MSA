package com.dasd412.api.readdiaryservice.adapter.out.persistence.diary;

import com.dasd412.api.readdiaryservice.domain.diary.DiabetesDiaryDocument;

import java.util.List;

public interface DiaryDocumentRepositoryCustom {

    List<DiabetesDiaryDocument>  getDiabetesDiariesOfWriter(String writerId);
}
