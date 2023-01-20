package com.dasd412.api.readdiaryservice.adapter.out.web.dto;

import com.dasd412.api.readdiaryservice.domain.diary.DiabetesDiaryDocument;
import com.dasd412.api.readdiaryservice.domain.diet.DietDocument;
import com.dasd412.api.readdiaryservice.domain.diet.EatTime;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

@Getter
public class BloodSugarBetweenTimeSpanDTO {

    private final int bloodSugar;

    private final EatTime eatTime;

    private final LocalDateTime dateTime;

    public BloodSugarBetweenTimeSpanDTO(DiabetesDiaryDocument diaryDocument, DietDocument dietDocument) {
        this.dateTime = diaryDocument.getWrittenTime();
        this.bloodSugar = dietDocument.getBloodSugar();
        this.eatTime = dietDocument.getEatTime();
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("blood sugar", bloodSugar)
                .append("eat time", eatTime)
                .append("date ", dateTime)
                .toString();
    }
}
