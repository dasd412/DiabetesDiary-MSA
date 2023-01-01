package com.dasd412.api.writerservice.adapter.in.message.handler;

import com.dasd412.api.writerservice.adapter.in.message.DiaryActionEnum;
import com.dasd412.api.writerservice.adapter.in.message.DiaryChannels;
import com.dasd412.api.writerservice.adapter.in.message.model.DiaryChangeModel;
import com.dasd412.api.writerservice.adapter.in.message.exception.NotSupportedKafkaMessageException;
import com.dasd412.api.writerservice.application.service.writer.UpdateWriterService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;

public class DiaryChangeHandler {

    private final UpdateWriterService updateWriterService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DiaryChangeHandler(UpdateWriterService updateWriterService) {
        this.updateWriterService = updateWriterService;
    }

    @StreamListener("inboundDiaryChanges")
    public void changeRelationWithDiary(DiaryChangeModel diaryChange) {
        logger.debug("Received an {} event for writer id {} and diary id {} at created time : {}", diaryChange.getAction(), diaryChange.getWriterId(), diaryChange.getDiaryId(), diaryChange.getLocalDateTimeFormat());

        //todo 나중에 레디스 등을 활용해서 결과적 일관성을 맞출 수 있어야 한다. diaryChange.getLocalDateTimeFormat()을 이용하면 될듯.
        if (DiaryActionEnum.compare(diaryChange.getAction()).equals(DiaryActionEnum.CREATED)) {
            updateWriterService.attachRelationWithDiary(diaryChange.getWriterId(), diaryChange.getDiaryId());
        } else if (DiaryActionEnum.compare(diaryChange.getAction()).equals(DiaryActionEnum.DELETED)) {
            updateWriterService.detachRelationWithDiary(diaryChange.getWriterId(), diaryChange.getDiaryId());
        } else {
            throw new NotSupportedKafkaMessageException("DiaryAction supported for writer service is only Create and Delete...");
        }
    }
}