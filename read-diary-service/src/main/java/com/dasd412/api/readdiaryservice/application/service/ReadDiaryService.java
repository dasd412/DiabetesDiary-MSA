package com.dasd412.api.readdiaryservice.application.service;

import com.dasd412.api.readdiaryservice.domain.diary.DiabetesDiaryDocument;

import java.util.List;
import java.util.Map;

public interface ReadDiaryService {

    List<DiabetesDiaryDocument> getDiabetesDiariesOfWriter(String writerId);

    List<DiabetesDiaryDocument> getDiariesBetweenTimeSpan(String writerId, Map<String, String> timeSpan);
}
