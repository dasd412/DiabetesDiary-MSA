package com.dasd412.api.writerservice.adapter.in.message.handler;

import com.dasd412.api.writerservice.adapter.in.message.DiaryActionEnum;
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
        logger.info("Received an {} event for writer id {} and diary id {} at created time : {}", diaryChange.getAction(), diaryChange.getWriterId(), diaryChange.getDiaryId(), diaryChange.getLocalDateTimeFormat());

        if (DiaryActionEnum.compare(diaryChange.getAction()).equals(DiaryActionEnum.CREATED)) {
            updateWriterService.attachRelationWithDiary(diaryChange.getWriterId(), diaryChange.getDiaryId());
        } else if (DiaryActionEnum.compare(diaryChange.getAction()).equals(DiaryActionEnum.DELETED)) {
            updateWriterService.detachRelationWithDiary(diaryChange.getWriterId(), diaryChange.getDiaryId());
        } else {
            throw new NotSupportedKafkaMessageException("DiaryAction supported for writer service is only Create and Delete...");
        }
    }
}
