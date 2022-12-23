package com.dasd412.api.writerservice.application.service;

public interface UpdateWriterService {

    void attachRelationWithDiary(Long writerId, Long diaryId);

    void detachRelationWithDiary(Long writerId, Long diaryId);
}
