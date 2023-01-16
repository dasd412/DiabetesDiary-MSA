package com.dasd412.api.readdiaryservice.domain.diet;

import com.dasd412.api.readdiaryservice.domain.food.FoodDocument;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Getter
@NoArgsConstructor
@Document(collection = "diet")
public class DietDocument {

    @Id
    private Long dietId;

    @Field("diary_id")
    private Long diaryId;

    @Field("writer_id")
    private Long writerId;

    @Field("eat_time")
    private EatTime eatTime;

    @Field("blood_sugar")
    private int bloodSugar;

    @Field("food_list")
    private List<FoodDocument> foodList;

    @Builder
    public DietDocument(Long dietId, Long diaryId, Long writerId, EatTime eatTime, int bloodSugar, List<FoodDocument> foodList) {
        checkArgument(dietId != null && dietId > 0, "id for reading must be positive integer.");
        checkArgument(diaryId != null && diaryId > 0, "reference for diary must be positive integer.");
        checkArgument(writerId != null && writerId > 0, "reference for writer must be positive integer.");
        checkArgument(bloodSugar >= 0 && bloodSugar <= 1000, "bloodSugar must be between 0 and 1000");
        checkArgument(foodList!=null,"document list must not be null");

        this.dietId = dietId;
        this.diaryId = diaryId;
        this.writerId = writerId;
        this.eatTime = eatTime;
        this.bloodSugar = bloodSugar;
        this.foodList = foodList;
    }

    public void addFoodList(List<FoodDocument> foodDocuments) {
        this.foodList.addAll(foodDocuments);
    }

    public void addFood(FoodDocument foodDocument) {
        this.foodList.add(foodDocument);
    }

    public void update(EatTime eatTime, int bloodSugar) {
        modifyEatTime(eatTime);
        modifyBloodSugar(bloodSugar);
    }

    public void modifyEatTime(EatTime eatTime) {
        this.eatTime = eatTime;
    }

    public void modifyBloodSugar(int bloodSugar) {
        checkArgument(bloodSugar >= 0 && bloodSugar <= 1000, "bloodSugar must be between 0 and 1000");
        this.bloodSugar = bloodSugar;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", dietId)
                .append("diary", diaryId)
                .append("eatTime", eatTime)
                .append("blood sugar", bloodSugar)
                .toString();
    }
}
