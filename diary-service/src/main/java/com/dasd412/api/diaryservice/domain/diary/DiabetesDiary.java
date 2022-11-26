package com.dasd412.api.diaryservice.domain.diary;

import com.dasd412.api.diaryservice.domain.BaseTimeEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

@Entity
@Table(name = "DiabetesDiary", uniqueConstraints = @UniqueConstraint(columnNames = {"diary_id"}))
public class DiabetesDiary extends BaseTimeEntity {

    @Id
    //todo GeneratedValue는 지워야할 수도
    @GeneratedValue
    @Column(name = "diary_id", columnDefinition = "bigint default 0")
    private Long diaryId;

    private Long writerId;

    @Column(name = "fpg")
    private int fastingPlasmaGlucose;

    private String remark;

    private LocalDateTime writtenTime;

//    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private final Set<Diet> dietList = new HashSet<>();

    /**
     * JPA 엔티티에는 기본 생성자가 필요하다.
     */
    public DiabetesDiary() {
    }

    //todo 나중에 지울 생성자. postman 실험용.
    public DiabetesDiary(Long writerId, int fastingPlasmaGlucose, String remark) {
        this.writerId=writerId;
        this.fastingPlasmaGlucose = fastingPlasmaGlucose;
        this.remark = remark;
    }

//    public DiabetesDiary(EntityId<DiabetesDiary, Long> diabetesDiaryEntityId, Writer writer, int fastingPlasmaGlucose, String remark, LocalDateTime writtenTime) {
//        checkArgument(fastingPlasmaGlucose >= 0 && fastingPlasmaGlucose <= 1000, "fastingPlasmaGlucose must be between 0 and 1000");
//        checkArgument(remark.length() <= 500, "remark length should be lower than 501");
//        this.diaryId = diabetesDiaryEntityId.getId();
//        this.writer = writer;
//        this.fastingPlasmaGlucose = fastingPlasmaGlucose;
//        this.remark = remark;
//        this.writtenTime = writtenTime;
//    }

    public Long getId() {
        return diaryId;
    }

    public int getFastingPlasmaGlucose() {
        return fastingPlasmaGlucose;
    }

    private void modifyFastingPlasmaGlucose(int fastingPlasmaGlucose) {
        checkArgument(fastingPlasmaGlucose >= 0 && fastingPlasmaGlucose <= 1000, "fastingPlasmaGlucose must be between 0 and 1000");
        this.fastingPlasmaGlucose = fastingPlasmaGlucose;
    }

    public String getRemark() {
        return remark;
    }

    private void modifyRemark(String remark) {
        checkArgument(remark.length() <= 500, "remark length should be lower than 501");
        this.remark = remark;
    }

    public LocalDateTime getWrittenTime() {
        return writtenTime;
    }

//    public List<Diet> getDietList() {
//        return new ArrayList<>(dietList);
//    }
//
//    public void addDiet(Diet diet) {
//        this.dietList.add(diet);
//
//        /* 무한 루프 체크 */
//        if (diet.getDiary() != this) {
//            diet.makeRelationWithDiary(this);
//        }
//    }

//    public Writer getWriter() {
//        return writer;
//    }
//
//    /**
//     * 복합키와 관련된 메서드이므로 엔티티 관계 설정이후엔 호출하면 안된다.
//     */
//    public void makeRelationWithWriter(Writer writer) {
//        this.writer = writer;
//        if (!writer.getDiaries().contains(this)) {
//            writer.getDiaries().add(this);
//        }
//    }

//    @Override
//    public String toString() {
//        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
//                .append("id", diaryId)
//                .append("writerId", writer)
//                .append("fpg", fastingPlasmaGlucose)
//                .append("remark", remark)
//                .append("written time", writtenTime)
//                .toString();
//    }

//    @Override
//    public int hashCode() {
//        return Objects.hash(writer, diaryId);
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (obj == null || getClass() != obj.getClass()) {
//            return false;
//        }
//        DiabetesDiary target = (DiabetesDiary) obj;
//        return Objects.equals(this.writer, target.writer) && Objects.equals(this.diaryId, target.diaryId);
//    }

    public void update(int fastingPlasmaGlucose, String remark) {
        modifyFastingPlasmaGlucose(fastingPlasmaGlucose);
        modifyRemark(remark);
    }
//
//    /**
//     * 연관 관계 제거 시에만 사용하는 메서드
//     */
//    public void removeDiet(Diet diet) {
//        checkArgument(this.dietList.contains(diet), "this diary dose not have the diet");
//        this.dietList.remove(diet);
//    }

}
