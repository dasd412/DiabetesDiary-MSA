package com.dasd412.api.readdiaryservice.adapter.in.msessage.handler;

import com.dasd412.api.readdiaryservice.adapter.in.msessage.WriterActionEnum;
import com.dasd412.api.readdiaryservice.adapter.in.msessage.exception.NotSupportedActionEnumException;
import com.dasd412.api.readdiaryservice.adapter.in.msessage.model.WriterChangeModel;
import com.dasd412.api.readdiaryservice.application.service.DiaryDataSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;

public class WriterChangeHandler {

    private final DiaryDataSyncService diaryDataSyncService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public WriterChangeHandler(DiaryDataSyncService diaryDataSyncService) {
        this.diaryDataSyncService = diaryDataSyncService;
    }

    @StreamListener("inboundWriterChangesToReader")
    public void deleteAllOfWriter(WriterChangeModel writerChangeModel) {

        logger.info("received an {} event for writer id {} at time {}", writerChangeModel.getAction(), writerChangeModel.getWriterId(), writerChangeModel.getLocalDateTimeFormat());

        if (WriterActionEnum.compare(writerChangeModel.getAction()).equals(WriterActionEnum.DELETED)) {
            logger.info("handle for deleting all data with writer...");
            //todo 전체 삭제 로직 넣기
        } else {
            throw new NotSupportedActionEnumException("Writer action supported for read diary service is only Delete...");
        }
    }
}
