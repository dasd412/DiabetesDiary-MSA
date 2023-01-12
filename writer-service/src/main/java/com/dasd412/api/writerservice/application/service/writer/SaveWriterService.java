package com.dasd412.api.writerservice.application.service.writer;

import com.dasd412.api.writerservice.application.service.security.vo.AuthenticationVO;
import com.dasd412.api.writerservice.domain.writer.Writer;

import java.util.concurrent.TimeoutException;

public interface SaveWriterService extends WriterService {
    Writer saveWriter(AuthenticationVO vo)throws TimeoutException;
}
