package com.dasd412.api.writerservice.application.service;

public interface SaveWriterService extends WriterService {

    Long saveWriterInTest(String name, String email);
}
