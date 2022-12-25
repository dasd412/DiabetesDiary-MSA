package com.dasd412.api.diaryservice.adapter.in.web.dto.update;

import com.dasd412.api.diaryservice.adapter.in.web.FoodListSize;
import com.dasd412.api.diaryservice.domain.diet.EatTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class DietUpdateRequestDTO {

    @NotNull
    @NotEmpty
    private final Long dietId;

    private final EatTime eatTime;

    private final int bloodSugar;

    //음식 리스트
    @Size(max = FoodListSize.FOOD_LIST_SIZE)
    private final List<FoodUpdateRequestDTO> foodList;

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("diet", dietId)
                .append("EatTime", eatTime)
                .append("bloodSugar", bloodSugar)
                .append("foodList", foodList.toString())
                .toString();
    }
}
