package com.dasd412.api.diaryservice.domain.food;

import com.dasd412.api.diaryservice.domain.diet.DietId;

import java.io.Serializable;
import java.util.Objects;

/**
 * 식단 복합키 식별자 클래스. 반드시 Serializable 구현해야 한다.
 * @serial
 */
public class FoodId implements Serializable {

    /**
     * Food.diet 매핑
     */
    private DietId diet;

    /**
     * Food.foodId 매핑
     */
    private Long foodId;

    /**
     * 식별자 클래스는 기본 생성자가 반드시 있어야 한다.
     */
    public FoodId() {
    }

    /**
     * 식별자 클래스는 반드시 equals 와 hashcode 를 재정의 해야한다.
     */
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
        FoodId target = (FoodId) obj;
        return Objects.equals(this.foodId, target.foodId) && Objects.equals(this.diet, target.diet);
    }

}
