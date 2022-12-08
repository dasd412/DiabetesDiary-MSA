package com.dasd412.api.diaryservice.message.source;

import com.dasd412.api.diaryservice.message.ActionEum;
import com.dasd412.api.diaryservice.message.model.DiaryChangeModel;
import com.dasd412.api.diaryservice.message.model.writer.ModelToWriter;
import com.dasd412.api.diaryservice.utils.trace.UserContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class KafkaSourceBean {

    private final Source source;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public KafkaSourceBean(Source source) {
        this.source = source;
    }

    public void publishDiaryChangeToWriter(ActionEum action, Long writerId, Long diaryId) {
        logger.debug("sending kafka message {} for publishing change of diary to writer-service", action);

        DiaryChangeModel changeModel = ModelToWriter.builder()
                .type(ModelToWriter.class.getTypeName())
                .action(action.toString())
                .writerId(writerId)
                .diaryId(diaryId)
                .correlationId(UserContext.getCorrelationId())
                .build();

        source.output().send(MessageBuilder.withPayload(changeModel).build());
    }

    //todo 스프링 마이크로 서비스 코딩 공작소 384p 부록을 보면, 메시징에 인자를 넣지 말고 마스터 DB에서 폴링하는 게 더 좋은 방법인듯..!
    public void publishDiaryChangeToReadDiary(ActionEum action) {
        logger.debug("sending kafka message {} for publishing change of diary to read-diary-service", action);
    }
}
