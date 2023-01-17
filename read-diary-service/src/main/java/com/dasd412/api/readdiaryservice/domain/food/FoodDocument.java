package com.dasd412.api.readdiaryservice.domain.food;

import com.dasd412.api.readdiaryservice.domain.StringMaxLength;
import com.querydsl.core.annotations.QueryEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import static com.google.common.base.Preconditions.checkArgument;

@Getter
@NoArgsConstructor
@QueryEntity
@Document(collection = "food")
public class FoodDocument {

    @Id
    private Long foodId;

    @Field("diet_id")
    private Long dietId;

    @Field("writer_id")
    private Long writerId;

    @Field("food_name")
    private String foodName;

    @Field("amount")
    private double amount;

    @Field("amount_unit")
    private AmountUnit amountUnit;

    @Builder
    public FoodDocument(Long foodId, Long dietId, Long writerId, String foodName, double amount, AmountUnit amountUnit) {
        checkArgument(foodId != null && foodId > 0, "id for reading must be positive integer.");
        checkArgument(writerId != null && writerId > 0, "reference for writer must be positive integer.");
        checkArgument(dietId != null && dietId > 0, "foreign key must be positive integer.");
        checkArgument(foodName.length() > 0 && foodName.length() <= StringMaxLength.FOOD_NAME, "food name length should be between 1 and 50");
        checkArgument(amount >= 0, "amount must be positive.");

        this.foodId = foodId;
        this.dietId = dietId;
        this.writerId = writerId;
        this.foodName = foodName;
        this.amount = amount;
        this.amountUnit = amountUnit;
    }

    public void update(String foodName) {
        modifyFoodName(foodName);
    }

    public void update(String foodName, double amount, AmountUnit amountUnit) {
        modifyFoodName(foodName);
        modifyAmount(amount);
        modifyAmountUnit(amountUnit);
    }

    public void modifyFoodName(String foodName) {
        checkArgument(foodName.length() > 0 && foodName.length() <= 50, "food name length should be between 1 and 50");
        this.foodName = foodName;
    }

    public void modifyAmount(double amount) {
        checkArgument(amount >= 0, "amount must be positive.");
        this.amount = amount;
    }

    public void modifyAmountUnit(AmountUnit amountUnit) {
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
}
