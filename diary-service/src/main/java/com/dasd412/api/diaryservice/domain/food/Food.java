package com.dasd412.api.diaryservice.domain.food;

import com.dasd412.api.diaryservice.domain.EntityId;
import com.dasd412.api.diaryservice.domain.StringMaxLength;
import com.dasd412.api.diaryservice.domain.diet.Diet;
import lombok.Builder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

@Entity
@Table(name = "Food")
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id")
    private Long foodId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Diet diet;

    @Column(name = "writer_id", nullable = false)
    private Long writerId;

    private String foodName;

    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(32) default 'NONE'")
    private AmountUnit amountUnit = AmountUnit.NONE;

    public Food() {
    }

    public Food(Diet diet, String foodName) {
        this(diet, foodName, 0);
    }

    public Food( Diet diet, String foodName, double amount) {
        this(diet, foodName, amount, AmountUnit.NONE);
    }

    @Builder
    public Food(Diet diet, String foodName, double amount, AmountUnit amountUnit) {
        checkArgument(foodName.length() > 0 && foodName.length() <= StringMaxLength.FOOD_NAME, "food name length should be between 1 and 50");
        checkArgument(amount >= 0, "amount must be positive.");
        checkArgument(diet.getWriterId() != null && diet.getWriterId() > 0, "foreign key must be positive integer.");
        this.diet = diet;
        this.writerId = diet.getWriterId();
        this.foodName = foodName;
        this.amount = amount;
        this.amountUnit = Objects.requireNonNullElse(amountUnit, AmountUnit.NONE);
    }

    public Long getFoodId() {
        return foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public double getAmount() {
        return amount;
    }

    public AmountUnit getAmountUnit() {
        return amountUnit;
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

    public Diet getDiet() {
        return diet;
    }

    /**
     * 연관 관계 편의 메소드.
     * 복합키와 관련된 메서드이므로 엔티티 관계 설정이후엔 호출하면 안된다.
     *
     * @param diet 연관 관계를 맺을 식단 엔티티
     */
    public void makeRelationWithDiet(Diet diet) {
        /* 기존 관계 삭제 */
        this.diet.getFoodList().remove(this);

        /* 무한 루프 체크 */
        this.diet = diet;
        if (!diet.getFoodList().contains(this)) {
            diet.getFoodList().add(this);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", foodId)
                .append("diet", diet)
                .append("foodName", foodName)
                .append("amount", amount)
                .append("amountUnit", amountUnit)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(diet, foodId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Food target = (Food) obj;
        return Objects.equals(this.foodId, target.foodId) && Objects.equals(this.diet, target.diet);
    }

    public void update(String foodName) {
        modifyFoodName(foodName);
    }

    public void update(String foodName, double amount, AmountUnit amountUnit) {
        modifyFoodName(foodName);
        modifyAmount(amount);
        modifyAmountUnit(amountUnit);
    }

}