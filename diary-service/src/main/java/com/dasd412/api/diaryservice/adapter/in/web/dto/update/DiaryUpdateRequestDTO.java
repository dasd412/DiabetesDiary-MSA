package com.dasd412.api.diaryservice.adapter.in.web.dto.update;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@RequiredArgsConstructor
@Builder
@Getter
public class DiaryUpdateRequestDTO {

    @NotNull
    private final Long writerId;

    //일지
    @NotNull
    @NotEmpty
    private final Long diaryId;

    private final int fastingPlasmaGlucose;

    private final String remark;

    //식단 리스트
    private final List<DietUpdateRequestDTO> dietList;

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("diary", diaryId)
                .append("fpg", fastingPlasmaGlucose)
                .append("remark", remark)
                .append("dietList", dietList.toString())
                .toString();
    }
}
