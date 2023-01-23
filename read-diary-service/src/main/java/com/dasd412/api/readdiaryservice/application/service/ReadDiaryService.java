package com.dasd412.api.readdiaryservice.application.service;

import com.dasd412.api.readdiaryservice.adapter.in.web.FoodPageVO;
import com.dasd412.api.readdiaryservice.adapter.out.web.dto.AllBloodSugarDTO;
import com.dasd412.api.readdiaryservice.adapter.out.web.dto.BloodSugarBetweenTimeSpanDTO;
import com.dasd412.api.readdiaryservice.adapter.out.web.dto.FoodBoardDTO;
import com.dasd412.api.readdiaryservice.domain.diary.DiabetesDiaryDocument;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ReadDiaryService {

    List<DiabetesDiaryDocument> getDiabetesDiariesOfWriter(String writerId);

    List<DiabetesDiaryDocument> getDiariesBetweenTimeSpan(String writerId, Map<String, String> timeSpan);

    List<AllBloodSugarDTO> getAllBloodSugarOfWriter(String writerId);

    List<BloodSugarBetweenTimeSpanDTO> getBloodSugarBetweenTimeSpan(String writerId, Map<String, String> timeSpan);

    Page<FoodBoardDTO> getFoodByPagination(String writerId, FoodPageVO foodPageVO);
}
