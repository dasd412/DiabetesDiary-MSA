package com.dasd412.api.readdiaryservice.adapter.out.web.dto;

import com.dasd412.api.readdiaryservice.domain.diary.DiabetesDiaryDocument;
import com.dasd412.api.readdiaryservice.domain.diet.DietDocument;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class FindOneDiaryDocumentDTO {

    private final Long diaryId;

    private final Long writerId;

    private final int fastingPlasmaGlucose;

    private final String remark;

    private final LocalDateTime writtenTime;

    private List<DietDocumentDTO> dietList;

    public FindOneDiaryDocumentDTO(DiabetesDiaryDocument document) {
        this.diaryId=document.getDiaryId();
        this.writerId=document.getWriterId();
        this.fastingPlasmaGlucose=document.getFastingPlasmaGlucose();
        this.remark=document.getRemark();
        this.writtenTime=document.getWrittenTime();
        toDTO(document.getDietList());
    }

    private void toDTO(List<DietDocument> dietDocumentList) {
        this.dietList=dietDocumentList.stream()
                .map(DietDocumentDTO::new).collect(Collectors.toList());
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
