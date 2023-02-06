package com.dasd412.api.diaryservice.adapter.out.message.model.readdiary;

import com.dasd412.api.diaryservice.adapter.out.message.model.DiaryChangeModel;
import com.dasd412.api.diaryservice.adapter.out.message.model.readdiary.dto.DiaryToReaderDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@Setter
@Builder
public class ModelToReader implements DiaryChangeModel {
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
