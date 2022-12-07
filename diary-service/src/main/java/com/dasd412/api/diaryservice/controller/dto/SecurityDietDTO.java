package com.dasd412.api.diaryservice.controller.dto;

import com.dasd412.api.diaryservice.controller.FoodListSize;
import com.dasd412.api.diaryservice.domain.diet.EatTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class SecurityDietDTO {

    private final EatTime eatTime;
    private final int bloodSugar;
    @Size(max = FoodListSize.FOOD_LIST_SIZE)
    private final List<SecurityFoodDTO> foodList;

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("EatTime", eatTime)
                .append("bloodSugar", bloodSugar)
                .append("foodList", foodList.toString())
                .toString();
    }
}
