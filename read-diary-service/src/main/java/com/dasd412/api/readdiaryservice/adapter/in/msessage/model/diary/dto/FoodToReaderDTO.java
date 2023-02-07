package com.dasd412.api.readdiaryservice.adapter.in.msessage.model.diary.dto;

import com.dasd412.api.readdiaryservice.domain.food.AmountUnit;
import com.dasd412.api.readdiaryservice.domain.food.FoodDocument;
import lombok.Builder;
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

    @Builder
    public FoodToReaderDTO(Long foodId, Long dietId, Long writerId, String foodName, double amount, AmountUnit amountUnit) {
        this.foodId = foodId;
        this.dietId = dietId;
        this.writerId = writerId;
        this.foodName = foodName;
        this.amount = amount;
        this.amountUnit = amountUnit;
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

    public FoodDocument toEntity() {
        return FoodDocument.builder()
                .foodId(this.foodId).dietId(this.dietId).writerId(this.writerId)
                .foodName(this.foodName)
                .amount(this.amount).amountUnit(this.amountUnit)
                .build();
    }
}
