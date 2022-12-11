package com.dasd412.api.writerservice.message.handler;

import com.dasd412.api.writerservice.domain.writer.WriterRepository;
import com.dasd412.api.writerservice.exception.NotSupportedKafkaMessageException;
import com.dasd412.api.writerservice.message.DiaryActionEnum;
import com.dasd412.api.writerservice.message.DiaryChannels;
import com.dasd412.api.writerservice.message.model.DiaryChangeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

@EnableBinding(DiaryChannels.class)
public class DiaryChangeHandler {

    private final WriterRepository writerRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DiaryChangeHandler(WriterRepository writerRepository) {
        this.writerRepository = writerRepository;
    }

    @StreamListener("inboundDiaryChanges")
    public void logSinkMessage(DiaryChangeModel diaryChange) {
        logger.debug("Received an {} event for writer id {} and diary id {}", diaryChange.getAction(), diaryChange.getWriterId(), diaryChange.getDiaryId());

        //todo 나중에 레디스 등을 활용해서 결과적 일관성을 맞출 수 있어야 한다.
        if (DiaryActionEnum.compare(diaryChange.getAction()).equals(DiaryActionEnum.CREATED)) {

        } else if (DiaryActionEnum.compare(diaryChange.getAction()).equals(DiaryActionEnum.DELETED)) {

        } else {
            throw new NotSupportedKafkaMessageException("DiaryAction supported for writer service is only Create and Delete...");
        }
    }
}
