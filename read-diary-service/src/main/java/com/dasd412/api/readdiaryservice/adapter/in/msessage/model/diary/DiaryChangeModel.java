package com.dasd412.api.readdiaryservice.adapter.in.msessage.model.diary;

import com.dasd412.api.readdiaryservice.adapter.in.msessage.model.diary.dto.DiaryToReaderDTO;
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

    private String correlationId;

    private String localDateTimeFormat;

    private DiaryToReaderDTO diaryToReaderDTO;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("Type", type)
                .append("action", action)
                .append("correlation id ", correlationId)
                .append("localDateTimeFormat", localDateTimeFormat)
                .append("diary", diaryToReaderDTO)
                .toString();
    }
}
