package com.dasd412.api.readdiaryservice.adapter.out.web.dto;

import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

@Getter
public class FoodBoardDTO {

    private final String foodName;

    private final int bloodSugar;

    private final LocalDateTime writtenTime;

    private final Long diaryId;

    public FoodBoardDTO(String foodName, int bloodSugar, LocalDateTime writtenTime, Long diaryId) {
        this.foodName = foodName;
        this.bloodSugar = bloodSugar;
        this.writtenTime = writtenTime;
        this.diaryId = diaryId;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("foodName", foodName)
                .append("bloodSugar", bloodSugar)
                .append("writtenTime", writtenTime)
                .append("diaryId", diaryId)
                .toString();
    }
}
