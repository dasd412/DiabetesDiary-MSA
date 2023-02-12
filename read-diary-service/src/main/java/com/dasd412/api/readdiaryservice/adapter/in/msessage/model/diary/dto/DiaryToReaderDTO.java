package com.dasd412.api.readdiaryservice.adapter.in.msessage.model.diary.dto;

import com.dasd412.api.readdiaryservice.domain.StringMaxLength;
import com.dasd412.api.readdiaryservice.domain.diary.DiabetesDiaryDocument;
import com.dasd412.api.readdiaryservice.domain.diet.DietDocument;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Getter
public class DiaryToReaderDTO {

    private final Long diaryId;

    private final Long writerId;

    private final int fastingPlasmaGlucose;

    private final String remark;

    private final LocalDateTime writtenTime;

    private final List<DietToReaderDTO> dietList;

    @Builder
    public DiaryToReaderDTO(Long diaryId, Long writerId, int fastingPlasmaGlucose, String remark, LocalDateTime writtenTime, List<DietToReaderDTO> dietToReaderDTOList) {
        checkArgument(diaryId != null && diaryId > 0, "id for reading must be positive integer.");
        checkArgument(writerId != null && writerId > 0, "reference must be positive integer.");
        checkArgument(fastingPlasmaGlucose >= 0 && fastingPlasmaGlucose <= 1000, "fastingPlasmaGlucose must be between 0 and 1000");
        checkArgument(remark.length() <= StringMaxLength.DIARY_REMARK, "remark length should be lower than 501");
        checkArgument(dietToReaderDTOList!=null,"document list must not be null");

        this.diaryId = diaryId;
        this.writerId = writerId;
        this.fastingPlasmaGlucose = fastingPlasmaGlucose;
        this.remark = remark;
        this.writtenTime = writtenTime;
        this.dietList = dietToReaderDTOList;
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

    public DiabetesDiaryDocument toEntity(List<DietDocument> dietDocumentList) {
        return DiabetesDiaryDocument.builder()
                .diaryId(this.diaryId).writerId(this.writerId)
                .fastingPlasmaGlucose(this.fastingPlasmaGlucose)
                .remark(this.remark)
                .writtenTime(this.writtenTime)
                .dietDocuments(dietDocumentList)
                .build();
    }
}
