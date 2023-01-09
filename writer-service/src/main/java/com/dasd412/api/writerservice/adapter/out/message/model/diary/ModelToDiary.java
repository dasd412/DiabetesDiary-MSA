package com.dasd412.api.writerservice.adapter.out.message.model.diary;

import com.dasd412.api.writerservice.adapter.out.message.model.WriterChangeModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@Setter
@Builder
public class ModelToDiary implements WriterChangeModel {

    private String type;

    private String action;

    private Long writerId;

    private String correlationId;

    private String localDateTimeFormat;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("Type", type)
                .append("action", action)
                .append("writer id ", writerId)
                .append("correlation id ", correlationId)
                .append("localDateTimeFormat", localDateTimeFormat)
                .toString();
    }
}
