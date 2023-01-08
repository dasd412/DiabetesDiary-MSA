package com.dasd412.api.writerservice.application.service.writer;

import com.dasd412.api.writerservice.application.service.security.vo.AuthenticationVO;

import java.util.concurrent.TimeoutException;

public interface SaveWriterService extends WriterService {
    Long saveWriter(AuthenticationVO vo)throws TimeoutException;
}
