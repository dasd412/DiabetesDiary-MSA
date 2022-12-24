package com.dasd412.api.diaryservice.domain.diet;

import com.dasd412.api.diaryservice.domain.EntityId;
import com.dasd412.api.diaryservice.domain.diary.DiabetesDiary;
import com.dasd412.api.diaryservice.domain.food.Food;
import lombok.Builder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

@Entity
@Table(name = "Diet")
public class Diet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "diet_id", columnDefinition = "bigint default 0")
    private Long dietId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private DiabetesDiary diary;

    @Column(name = "writer_id", nullable = false)
    private Long writerId;

    @Enumerated(EnumType.STRING)
    private EatTime eatTime;

    private int bloodSugar;

    @OneToMany(mappedBy = "diet", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final List<Food> foodList = new ArrayList<>();

    public Diet() {
    }

    @Builder
    public Diet(DiabetesDiary diary, EatTime eatTime, int bloodSugar) {
        checkArgument(bloodSugar >= 0 && bloodSugar <= 1000, "bloodSugar must be between 0 and 1000");
        checkArgument(diary.getWriterId() != null && diary.getWriterId() > 0, "foreign key must be positive integer.");
        this.diary = diary;
        this.writerId = diary.getWriterId();
        this.eatTime = eatTime;
        this.bloodSugar = bloodSugar;
    }

    public Long getDietId() {
        return dietId;
    }

    public EatTime getEatTime() {
        return eatTime;
    }

    public void modifyEatTime(EatTime eatTime) {
        this.eatTime = eatTime;
    }

    public int getBloodSugar() {
        return bloodSugar;
    }

    public void modifyBloodSugar(int bloodSugar) {
        checkArgument(bloodSugar >= 0 && bloodSugar <= 1000, "bloodSugar must be between 0 and 1000");
        this.bloodSugar = bloodSugar;
    }

    public List<Food> getFoodList() {
        return new ArrayList<>(foodList);
    }

    public Long getWriterId() {
        return writerId;
    }

    public void addFood(Food food) {
        this.foodList.add(food);
        //무한 루프에 빠지지 않도록 체크
        if (food.getDiet() != this) {
            food.makeRelationWithDiet(this);
        }
    }

    public DiabetesDiary getDiary() {
        return diary;
    }

    /**
     * 연관 관계 편의 메소드.
     * 복합키와 관련된 메서드이므로 엔티티 관계 설정이후엔 호출하면 안된다.
     */
    public void makeRelationWithDiary(DiabetesDiary diary) {
        //무한 루프 체크
        this.diary = diary;
        if (!diary.getDietList().contains(this)) {
            diary.getDietList().add(this);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", dietId)
                .append("diary", diary)
                .append("eatTime", eatTime)
                .append("blood sugar", bloodSugar)
                .toString();
    }

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
        Diet target = (Diet) obj;
        return Objects.equals(this.dietId, target.dietId) && Objects.equals(this.diary, target.diary);
    }

    public void update(EatTime eatTime, int bloodSugar) {
        modifyEatTime(eatTime);
        modifyBloodSugar(bloodSugar);
    }

    /**
     * 연관 관계 제거 시에만 사용
     */
    public void removeFood(Food food) {
        checkArgument(this.foodList.contains(food), "this diet dose not have the food");
        this.foodList.remove(food);
    }
}