package com.dasd412.api.diaryservice.adapter.out.message.model.writer;

import com.dasd412.api.diaryservice.adapter.out.message.model.DiaryChangeModel;

import lombok.Builder;
import lombok.Getter;

import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@Setter
@Builder
public class ModelToWriter implements DiaryChangeModel {

    private String type;

    private String action;

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
