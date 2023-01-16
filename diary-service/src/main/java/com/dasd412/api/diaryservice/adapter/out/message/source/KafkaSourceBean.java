package com.dasd412.api.diaryservice.adapter.out.message.source;

import com.dasd412.api.diaryservice.adapter.out.message.ActionEnum;
import com.dasd412.api.diaryservice.adapter.out.message.DiaryChannels;
import com.dasd412.api.diaryservice.adapter.out.message.model.DiaryChangeModel;
import com.dasd412.api.diaryservice.adapter.out.message.model.writer.ModelToWriter;
import com.dasd412.api.diaryservice.common.utils.trace.UserContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.support.MessageBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

public class KafkaSourceBean {

    private final DiaryChannels diaryChannels;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public KafkaSourceBean(DiaryChannels diaryChannels) {
        this.diaryChannels = diaryChannels;
    }

    public void publishDiaryChangeToWriter(ActionEnum action, Long writerId, Long diaryId)  throws TimeoutException {
        logger.debug("sending kafka message {} for publishing change of diary to writer-service", action);

        DiaryChangeModel changeModel = ModelToWriter.builder()
                .type(ModelToWriter.class.getTypeName())
                .action(action.toString())
                .writerId(writerId)
                .diaryId(diaryId)
                .correlationId(UserContext.getCorrelationId())
                .localDateTimeFormat(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();

        diaryChannels.getOutputChannel().send(MessageBuilder.withPayload(changeModel).build());
    }

    //todo 스프링 마이크로 서비스 코딩 공작소 384p 부록을 보면, 메시징에 인자를 넣지 말고 마스터 DB에서 폴링하는 게 더 좋은 방법인듯..!
    public void publishDiaryChangeToReadDiary(ActionEnum action) {
        logger.debug("sending kafka message {} for publishing change of diary to read-diary-service", action);
    }
}
