package com.dasd412.api.diaryservice.adapter.in.message.handler;

import com.dasd412.api.diaryservice.adapter.in.message.WriterActionEnum;
import com.dasd412.api.diaryservice.adapter.in.message.exception.NotSupportedActionEnumException;
import com.dasd412.api.diaryservice.adapter.in.message.model.WriterChangeModel;
import com.dasd412.api.diaryservice.application.service.DeleteDiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;

public class WriterChangeHandler {

    private final DeleteDiaryService deleteDiaryService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public WriterChangeHandler(DeleteDiaryService deleteDiaryService) {
        this.deleteDiaryService = deleteDiaryService;
    }

    @StreamListener("inboundWriterChanges")
    public void deleteAllOfWriter(WriterChangeModel writerChangeModel) {
        logger.info("received an {} event for writer id {} at time {}", writerChangeModel.getAction(), writerChangeModel.getWriterId(), writerChangeModel.getLocalDateTimeFormat());

        if (WriterActionEnum.compare(writerChangeModel.getAction()).equals(WriterActionEnum.DELETED)) {
            deleteDiaryService.deleteAllOfWriter(writerChangeModel.getWriterId());
        } else {
            throw new NotSupportedActionEnumException("Writer action supported for diary service is only Delete...");
        }
    }
}
