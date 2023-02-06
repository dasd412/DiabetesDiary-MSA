package com.dasd412.api.readdiaryservice.adapter.in.msessage.model.diary.dto;

import com.dasd412.api.readdiaryservice.domain.diet.EatTime;
import lombok.Builder;
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

    @Builder
    public DietToReaderDTO(Long dietId, Long diaryId, Long writerId, EatTime eatTime, int bloodSugar, List<FoodToReaderDTO> foodList) {
        this.dietId = dietId;
        this.diaryId = diaryId;
        this.writerId = writerId;
        this.eatTime = eatTime;
        this.bloodSugar = bloodSugar;
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
