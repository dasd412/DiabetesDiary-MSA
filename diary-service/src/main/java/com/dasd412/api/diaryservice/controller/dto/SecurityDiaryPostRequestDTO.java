package com.dasd412.api.diaryservice.controller.dto;

import com.dasd412.api.diaryservice.controller.FoodListSize;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@RequiredArgsConstructor
@Builder
@Getter
public class SecurityDiaryPostRequestDTO {

    @NotEmpty
    @NotNull
    private final Long writerId;
    
    private final int fastingPlasmaGlucose;

    private final String remark;

    @NotEmpty
    @NotNull
    private final String year;

    @NotEmpty
    @NotNull
    private final String month;

    @NotEmpty
    @NotNull
    private final String day;

    @NotEmpty
    @NotNull
    private final String hour;

    @NotEmpty
    @NotNull
    private final String minute;

    @NotEmpty
    @NotNull
    private final String second;

    private final int breakFastSugar;
    private final int lunchSugar;
    private final int dinnerSugar;

    @Size(max = FoodListSize.FOOD_LIST_SIZE)
    private final List<SecurityFoodDTO> breakFastFoods;

    @Size(max = FoodListSize.FOOD_LIST_SIZE)
    private final List<SecurityFoodDTO> lunchFoods;

    @Size(max = FoodListSize.FOOD_LIST_SIZE)
    private final List<SecurityFoodDTO> dinnerFoods;

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("fpg", fastingPlasmaGlucose)
                .append("remark", remark)
                .append("year", year)
                .append("month", month)
                .append("day", day)
                .append("hour", hour)
                .append("minute", minute)
                .append("second", second)
                .append("breakFastSugar", breakFastSugar)
                .append("lunchSugar", lunchSugar)
                .append("dinnerSugar", dinnerSugar)
                .append("breakFastFoods", breakFastFoods)
                .append("lunchFoods", lunchFoods)
                .append("dinnerFoods", dinnerFoods)
                .toString();
    }
}