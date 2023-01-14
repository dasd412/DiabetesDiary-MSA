package com.dasd412.api.readdiaryservice.adapter.in.msessage.handler;

import com.dasd412.api.readdiaryservice.application.service.DiaryDataSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiaryChangeHandler {

    private final DiaryDataSyncService diaryDataSyncService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DiaryChangeHandler(DiaryDataSyncService diaryDataSyncService) {
        this.diaryDataSyncService = diaryDataSyncService;
    }
}
