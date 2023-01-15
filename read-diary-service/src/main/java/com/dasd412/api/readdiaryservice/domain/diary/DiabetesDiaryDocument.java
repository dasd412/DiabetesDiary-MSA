package com.dasd412.api.readdiaryservice.domain.diary;

import com.dasd412.api.readdiaryservice.domain.StringMaxLength;
import com.dasd412.api.readdiaryservice.domain.diet.DietDocument;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Getter
@Document(collation = "diabetes_diary")
public class DiabetesDiaryDocument {

    @Id
    private final Long diaryId;

    @Field("writer_id")
    private final long writerId;

    @Field("fpg")
    private int fastingPlasmaGlucose;

    @Field("remark")
    private String remark;

    @Field("written_time")
    private final LocalDateTime writtenTime;

    private final List<DietDocument> dietList;

    @Builder
    public DiabetesDiaryDocument(Long diaryId, Long writerId, int fastingPlasmaGlucose, String remark, LocalDateTime writtenTime, List<DietDocument> dietDocuments) {
        checkArgument(diaryId != null && diaryId > 0, "id for reading must be positive integer.");
        checkArgument(writerId != null && writerId > 0, "reference must be positive integer.");
        checkArgument(fastingPlasmaGlucose >= 0 && fastingPlasmaGlucose <= 1000, "fastingPlasmaGlucose must be between 0 and 1000");
        checkArgument(remark.length() <= StringMaxLength.DIARY_REMARK, "remark length should be lower than 501");
        checkArgument(dietDocuments!=null,"document list must not be null");

        this.diaryId = diaryId;
        this.writerId = writerId;
        this.fastingPlasmaGlucose = fastingPlasmaGlucose;
        this.remark = remark;
        this.writtenTime = writtenTime;
        this.dietList = dietDocuments;
    }

    public void addDietList(List<DietDocument> dietDocuments) {
        this.dietList.addAll(dietDocuments);
    }

    public void addDiet(DietDocument dietDocument) {
        this.dietList.add(dietDocument);
    }

    public void update(int fastingPlasmaGlucose, String remark) {
        modifyFastingPlasmaGlucose(fastingPlasmaGlucose);
        modifyRemark(remark);
    }

    public void modifyFastingPlasmaGlucose(int fastingPlasmaGlucose) {
        checkArgument(fastingPlasmaGlucose >= 0 && fastingPlasmaGlucose <= 1000, "fastingPlasmaGlucose must be between 0 and 1000");
        this.fastingPlasmaGlucose = fastingPlasmaGlucose;
    }

    public void modifyRemark(String remark) {
        checkArgument(remark.length() <= StringMaxLength.DIARY_REMARK, "remark length should be lower than 501");
        this.remark = remark;
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
