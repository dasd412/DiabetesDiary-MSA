package com.dasd412.api.writerservice.application.service.writer;

import com.dasd412.api.writerservice.application.service.vo.AuthenticationVO;
import com.dasd412.api.writerservice.domain.writer.Writer;

import java.util.concurrent.TimeoutException;

public interface SaveWriterService extends WriterService {

    public Long saveWriter(AuthenticationVO vo)throws TimeoutException;
}
