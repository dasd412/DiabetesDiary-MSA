package com.dasd412.api.diaryservice.adapter.out.message.model.readdiary.dto;

import com.dasd412.api.diaryservice.domain.food.AmountUnit;
import com.dasd412.api.diaryservice.domain.food.Food;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
public class FoodToReaderDTO {

    private final Long foodId;

    private final Long dietId;

    private final Long writerId;

    private final String foodName;

    private final double amount;

    private final AmountUnit amountUnit;

    public FoodToReaderDTO(Food food,Long dietId) {
        this.foodId = food.getFoodId();
        this.dietId = dietId;
        this.writerId = food.getWriterId();
        this.foodName = food.getFoodName();
        this.amount = food.getAmount();
        this.amountUnit = food.getAmountUnit();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", foodId)
                .append("diet", dietId)
                .append("foodName", foodName)
                .append("amount", amount)
                .append("amountUnit", amountUnit)
                .toString();
    }
}
