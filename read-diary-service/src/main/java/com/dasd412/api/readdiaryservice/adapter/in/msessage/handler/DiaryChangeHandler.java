package com.dasd412.api.readdiaryservice.adapter.in.msessage.handler;

import com.dasd412.api.readdiaryservice.adapter.in.msessage.DiaryActionEnum;
import com.dasd412.api.readdiaryservice.adapter.in.msessage.exception.NotSupportedActionEnumException;
import com.dasd412.api.readdiaryservice.adapter.in.msessage.model.diary.DiaryChangeModel;
import com.dasd412.api.readdiaryservice.application.service.DiaryDataSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;

public class DiaryChangeHandler {

    private final DiaryDataSyncService diaryDataSyncService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DiaryChangeHandler(DiaryDataSyncService diaryDataSyncService) {
        this.diaryDataSyncService = diaryDataSyncService;
    }

    //todo if 문 각각 완성해야 함.
    @StreamListener("inboundDiaryChangesToReader")
    public void syncDataOfDiary(DiaryChangeModel diaryChangeModel) {
        logger.info("received an message of diary cud service");

        if (DiaryActionEnum.compare(diaryChangeModel.getAction()).equals(DiaryActionEnum.CREATED)) {
            diaryDataSyncService.createDocument(diaryChangeModel.getDiaryToReaderDTO());

        } else if (DiaryActionEnum.compare(diaryChangeModel.getAction()).equals(DiaryActionEnum.UPDATED)) {

        } else if (DiaryActionEnum.compare(diaryChangeModel.getAction()).equals(DiaryActionEnum.DELETED)) {

        } else {
            throw new NotSupportedActionEnumException("diary actions supported for read diary service are create, update, delete...");
        }
    }
}
