package com.dasd412.api.writerservice.application.service.writer;

public interface DeleteWriterService {

    Long removeWriter(String accessToken);

    void sendMessageToOtherService(Long writerId);
}
