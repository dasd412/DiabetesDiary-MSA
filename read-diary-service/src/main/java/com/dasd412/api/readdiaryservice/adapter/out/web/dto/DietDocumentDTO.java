package com.dasd412.api.readdiaryservice.adapter.out.web.dto;

import com.dasd412.api.readdiaryservice.domain.diet.DietDocument;
import com.dasd412.api.readdiaryservice.domain.diet.EatTime;
import com.dasd412.api.readdiaryservice.domain.food.FoodDocument;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class DietDocumentDTO {

    private final Long dietId;

    private final Long diaryId;

    private final Long writerId;

    private final EatTime eatTime;

    private final int bloodSugar;

    private List<FoodDocumentDTO> foodList;

    public DietDocumentDTO(DietDocument dietDocument) {
        this.dietId = dietDocument.getDietId();
        this.diaryId = dietDocument.getDiaryId();
        this.writerId = dietDocument.getWriterId();
        this.eatTime = dietDocument.getEatTime();
        this.bloodSugar = dietDocument.getBloodSugar();
        toDTO(dietDocument.getFoodList());
    }

    private void toDTO(List<FoodDocument>foodDocumentList){
        this.foodList=foodDocumentList.stream()
                .map(FoodDocumentDTO::new).collect(Collectors.toList());
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
}
