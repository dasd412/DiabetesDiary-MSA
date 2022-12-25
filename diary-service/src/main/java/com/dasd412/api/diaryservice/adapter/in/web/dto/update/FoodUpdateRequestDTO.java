package com.dasd412.api.diaryservice.adapter.in.web.dto.update;


import com.dasd412.api.diaryservice.domain.food.AmountUnit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor/* <- cannot deserialize from Object value (no delegate- or property-based Creator) 에러를 막기 위해 넣음. */
public class FoodUpdateRequestDTO {

    @NotNull
    private Long foodId;

    private String foodName;

    private double amount;

    private String amountUnit;

    public FoodUpdateRequestDTO(Long foodId, String foodName, double amount, AmountUnit amountUnit) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.amount = amount;
        this.amountUnit = amountUnit.name();
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("foodId", foodId)
                .append("foodName", foodName)
                .append("amount", amount)
                .append("amountUnit", amountUnit)
                .toString();
    }
}
