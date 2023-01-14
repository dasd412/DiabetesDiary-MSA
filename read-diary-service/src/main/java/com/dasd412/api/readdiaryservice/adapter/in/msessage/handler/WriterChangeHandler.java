package com.dasd412.api.readdiaryservice.adapter.in.msessage.handler;

import com.dasd412.api.readdiaryservice.application.service.DiaryDataSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WriterChangeHandler {

    private final DiaryDataSyncService diaryDataSyncService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public WriterChangeHandler(DiaryDataSyncService diaryDataSyncService) {
        this.diaryDataSyncService = diaryDataSyncService;
    }
}
