package com.dasd412.api.diaryservice.controller.dto;

import com.dasd412.api.diaryservice.domain.food.AmountUnit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@NoArgsConstructor /* <- cannot deserialize from Object value (no delegate- or property-based Creator) 에러를 막기 위해 넣음. */
public class SecurityFoodDTO {

    private String foodName;
    private double amount;
    private AmountUnit amountUnit;

    public SecurityFoodDTO(String foodName, double amount) {
        this.foodName = foodName;
        this.amount = amount;
        this.amountUnit = AmountUnit.g;
    }

    public SecurityFoodDTO(String foodName, double amount, AmountUnit amountUnit) {
        this.foodName = foodName;
        this.amount = amount;
        this.amountUnit = amountUnit;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("foodName", foodName)
                .append("amount", amount)
                .append("amountUnit", amountUnit)
                .toString();

    }
}