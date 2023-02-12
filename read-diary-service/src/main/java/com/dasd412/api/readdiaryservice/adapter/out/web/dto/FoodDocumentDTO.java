package com.dasd412.api.readdiaryservice.adapter.out.web.dto;

import com.dasd412.api.readdiaryservice.domain.food.AmountUnit;
import com.dasd412.api.readdiaryservice.domain.food.FoodDocument;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
public class FoodDocumentDTO {

    private final Long foodId;

    private final Long dietId;

    private final Long writerId;

    private final String foodName;

    private final double amount;

    private final AmountUnit amountUnit;

    public FoodDocumentDTO(FoodDocument foodDocument) {
        this.foodId = foodDocument.getFoodId();
        this.dietId = foodDocument.getDietId();
        this.writerId = foodDocument.getWriterId();
        this.foodName = foodDocument.getFoodName();
        this.amount = foodDocument.getAmount();
        this.amountUnit = foodDocument.getAmountUnit();
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
