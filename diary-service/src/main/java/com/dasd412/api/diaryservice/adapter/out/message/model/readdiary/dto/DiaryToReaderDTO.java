package com.dasd412.api.diaryservice.adapter.out.message.model.readdiary.dto;

import com.dasd412.api.diaryservice.domain.diary.DiabetesDiary;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class DiaryToReaderDTO {

    private final Long diaryId;

    private final Long writerId;

    private final int fastingPlasmaGlucose;

    private final String remark;

    private final LocalDateTime writtenTime;

    private List<DietToReaderDTO> dietList;

    public DiaryToReaderDTO(Long diaryId, Long writerId) {
        this.diaryId = diaryId;
        this.writerId = writerId;
        this.fastingPlasmaGlucose = 0;
        this.remark = "";
        this.writtenTime = LocalDateTime.now();
    }

    public DiaryToReaderDTO(DiabetesDiary diary) {
        this.diaryId = diary.getDiaryId();
        this.writerId = diary.getWriterId();
        this.fastingPlasmaGlucose = diary.getFastingPlasmaGlucose();
        this.remark = diary.getRemark();
        this.writtenTime = diary.getWrittenTime();
    }

    public void addDietList(List<DietToReaderDTO> dietList) {
        this.dietList = dietList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", diaryId)
                .append("writerId", writerId)
                .append("fpg", fastingPlasmaGlucose)
                .append("remark", remark)
                .append("written time", writtenTime)
                .toString();
    }
}
