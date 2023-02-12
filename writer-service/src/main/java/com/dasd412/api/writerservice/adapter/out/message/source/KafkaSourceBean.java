package com.dasd412.api.writerservice.adapter.out.message.source;

import com.dasd412.api.writerservice.adapter.out.message.ActionEnum;
import com.dasd412.api.writerservice.adapter.out.message.WriterChannels;
import com.dasd412.api.writerservice.adapter.out.message.WriterToReaderChannels;
import com.dasd412.api.writerservice.adapter.out.message.model.WriterChangeModel;
import com.dasd412.api.writerservice.adapter.out.message.model.diary.ModelToDiary;
import com.dasd412.api.writerservice.adapter.out.message.model.readdiary.ModelToReader;
import com.dasd412.api.writerservice.common.utils.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.support.MessageBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class KafkaSourceBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WriterChannels writerChannels;

    private final WriterToReaderChannels writerToReaderChannels;

    public KafkaSourceBean(WriterChannels writerChannels, WriterToReaderChannels writerToReaderChannels) {
        this.writerChannels = writerChannels;
        this.writerToReaderChannels = writerToReaderChannels;
    }

    public void publishWriterChangeToDiary(ActionEnum action, Long writerId) {
        logger.info("sending kafka message to cud service for publishing withdrawal of writer:{}", writerId);

        WriterChangeModel writerChangeModel = ModelToDiary.builder()
                .type(ModelToDiary.class.getTypeName())
                .action(action.toString())
                .writerId(writerId)
                .correlationId(UserContext.getCorrelationId())
                .localDateTimeFormat(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();

        writerChannels.getOutputChannel().send(MessageBuilder.withPayload(writerChangeModel).build());
    }

    public void publishWriterChangeToReader(ActionEnum action, Long writerId) {
        logger.info("sending kafka message to reader service for publishing withdrawal of writer:{}", writerId);

        WriterChangeModel writerChangeModel = ModelToReader.builder()
                .type(ModelToReader.class.getTypeName())
                .action(action.toString())
                .writerId(writerId)
                .correlationId(UserContext.getCorrelationId())
                .localDateTimeFormat(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();

        writerToReaderChannels.getOutputChannel().send(MessageBuilder.withPayload(writerChangeModel).build());
    }
}
