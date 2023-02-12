package com.dasd412.api.readdiaryservice.adapter.in.msessage.model.diary.dto;

import com.dasd412.api.readdiaryservice.domain.diet.DietDocument;
import com.dasd412.api.readdiaryservice.domain.diet.EatTime;
import com.dasd412.api.readdiaryservice.domain.food.FoodDocument;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Getter
public class DietToReaderDTO {

    private final Long dietId;

    private final Long diaryId;

    private final Long writerId;

    private final EatTime eatTime;

    private final int bloodSugar;

    private final List<FoodToReaderDTO> foodList;

    @Builder
    public DietToReaderDTO(Long dietId, Long diaryId, Long writerId, EatTime eatTime, int bloodSugar, List<FoodToReaderDTO> foodList) {
        checkArgument(dietId != null && dietId > 0, "id for reading must be positive integer.");
        checkArgument(diaryId != null && diaryId > 0, "reference for diary must be positive integer.");
        checkArgument(writerId != null && writerId > 0, "reference for writer must be positive integer.");
        checkArgument(bloodSugar >= 0 && bloodSugar <= 1000, "bloodSugar must be between 0 and 1000");

        this.dietId = dietId;
        this.diaryId = diaryId;
        this.writerId = writerId;
        this.eatTime = eatTime;
        this.bloodSugar = bloodSugar;
        this.foodList = foodList;
    }

    public List<FoodToReaderDTO> getFoodList() {
        if(this.foodList==null){
            return Collections.emptyList();
        }
        return foodList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", dietId)
                .append("diary", diaryId)
                .append("eatTime", eatTime)
                .append("blood sugar", bloodSugar)
                .toString();
    }

    public DietDocument toEntity(List<FoodDocument>foodDocumentList){
        return DietDocument.builder()
                .dietId(this.dietId).diaryId(this.diaryId).writerId(this.writerId)
                .eatTime(this.eatTime).bloodSugar(this.bloodSugar)
                .foodList(foodDocumentList)
                .build();
    }
}
