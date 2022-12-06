package com.dasd412.api.diaryservice.domain.diet;

import java.io.Serializable;
import java.util.Objects;

public class DietId implements Serializable {

    /**
     * Diet.diary 매핑
     */
    private Long diary;

    /**
     * Diet.dietId 매핑
     */
    private Long dietId;

    /**
     * 식별자 클래스는 기본 생성자가 반드시 있어야 한다.
     */
    public DietId() {
    }

    /**
     * 식별자 클래스는 반드시 equals 와 hashcode 를 재정의 해야한다.
     */
    @Override
    public int hashCode() {
        return Objects.hash(diary, dietId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DietId target = (DietId) obj;
        return Objects.equals(this.dietId, target.dietId) && Objects.equals(this.diary, target.diary);
    }

}