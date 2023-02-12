package com.dasd412.api.diaryservice.adapter.out.message.model.readdiary.dto;

import com.dasd412.api.diaryservice.domain.diet.Diet;
import com.dasd412.api.diaryservice.domain.diet.EatTime;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@Getter
public class DietToReaderDTO {

    private final Long dietId;

    private final Long diaryId;

    private final Long writerId;

    private final EatTime eatTime;

    private final int bloodSugar;

    private final List<FoodToReaderDTO> foodList;

    public DietToReaderDTO(Diet diet, Long diaryId, List<FoodToReaderDTO> foodList) {
        this.dietId = diet.getDietId();
        this.diaryId = diaryId;
        this.writerId = diet.getWriterId();
        this.eatTime = diet.getEatTime();
        this.bloodSugar = diet.getBloodSugar();
        this.foodList = foodList;
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
