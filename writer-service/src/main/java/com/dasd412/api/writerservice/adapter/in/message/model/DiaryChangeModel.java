package com.dasd412.api.writerservice.adapter.in.message.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@Setter
@Builder
public class DiaryChangeModel {

    private String type;

    private String action;
    /*
    CUD Diary service에서 writer service로 메시지 전달을 할 땐, writer 엔티티와 diary 엔티티 간의 연관 관계를 다루게 된다.
    Create 일 경우, 연관관계 맺기를 하고 Update는 writer 한테는 상관 없고  Delete 일 경우는 연관 관계를 끊어야 한다.
     */
    private Long writerId;

    private Long diaryId;

    private String correlationId;

    private String localDateTimeFormat;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("Type", type)
                .append("action", action)
                .append("writer id ", writerId)
                .append("diary id", diaryId)
                .append("correlation id ", correlationId)
                .append("localDateTimeFormat",localDateTimeFormat)
                .toString();
    }
}
