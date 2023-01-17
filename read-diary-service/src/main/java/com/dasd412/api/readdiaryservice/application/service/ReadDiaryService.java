package com.dasd412.api.readdiaryservice.application.service;

import com.dasd412.api.readdiaryservice.domain.diary.DiabetesDiaryDocument;

import java.util.List;

public interface ReadDiaryService {

    List<DiabetesDiaryDocument> getDiabetesDiariesOfWriter(String writerId);
}
