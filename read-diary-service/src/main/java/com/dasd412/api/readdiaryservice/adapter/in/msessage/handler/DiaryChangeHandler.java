package com.dasd412.api.readdiaryservice.adapter.in.msessage.handler;

import com.dasd412.api.readdiaryservice.adapter.in.msessage.DiaryActionEnum;
import com.dasd412.api.readdiaryservice.adapter.in.msessage.exception.NotSupportedActionEnumException;
import com.dasd412.api.readdiaryservice.adapter.in.msessage.model.diary.DiaryChangeModel;
import com.dasd412.api.readdiaryservice.application.service.DiaryDataSyncService;
import com.dasd412.api.readdiaryservice.application.service.ReadDiaryService;
import com.dasd412.api.readdiaryservice.domain.diary.DiabetesDiaryDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;

public class DiaryChangeHandler {

    private final DiaryDataSyncService diaryDataSyncService;

    private final ReadDiaryService readDiaryService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DiaryChangeHandler(DiaryDataSyncService diaryDataSyncService, ReadDiaryService readDiaryService) {
        this.diaryDataSyncService = diaryDataSyncService;
        this.readDiaryService = readDiaryService;
    }

    @StreamListener("inboundDiaryChangesToReader")
    public void syncDataOfDiary(DiaryChangeModel diaryChangeModel) {
        logger.info("received an message of diary cud service");

        if (DiaryActionEnum.compare(diaryChangeModel.getAction()).equals(DiaryActionEnum.CREATED)) {
            diaryDataSyncService.createDocument(diaryChangeModel.getDiaryToReaderDTO());

        } else if (DiaryActionEnum.compare(diaryChangeModel.getAction()).equals(DiaryActionEnum.UPDATED)) {

            DiabetesDiaryDocument targetDiary = readDiaryService.getOneDiaryDocument(
                    String.valueOf(diaryChangeModel.getDiaryToReaderDTO().getWriterId()), diaryChangeModel.getDiaryToReaderDTO().getDiaryId());

            diaryDataSyncService.updateDocument(targetDiary,diaryChangeModel.getDiaryToReaderDTO());

        } else if (DiaryActionEnum.compare(diaryChangeModel.getAction()).equals(DiaryActionEnum.DELETED)) {

            DiabetesDiaryDocument targetDiary = readDiaryService.getOneDiaryDocument(
                    String.valueOf(diaryChangeModel.getDiaryToReaderDTO().getWriterId()), diaryChangeModel.getDiaryToReaderDTO().getDiaryId());

            diaryDataSyncService.deleteDocument(targetDiary);

        } else {
            throw new NotSupportedActionEnumException("diary actions supported for read diary service are create, update, delete...");
        }
    }
}
