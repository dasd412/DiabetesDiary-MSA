package com.dasd412.api.readdiaryservice.adapter.out.web.dto;

import com.dasd412.api.readdiaryservice.domain.diary.DiabetesDiaryDocument;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

@Getter
public class FpgBetweenDTO {

    private final int fastingPlasmaGlucose;

    private final LocalDateTime timeStamp;

    public FpgBetweenDTO(DiabetesDiaryDocument document) {
        this.fastingPlasmaGlucose = document.getFastingPlasmaGlucose();
        this.timeStamp = document.getWrittenTime();
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("fpg", this.fastingPlasmaGlucose)
                .append("timeStamp", this.timeStamp)
                .toString();
    }
}
