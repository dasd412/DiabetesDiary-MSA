package com.dasd412.api.writerservice.application.service;

import com.dasd412.api.writerservice.domain.writer.Writer;

public interface FindWriterService extends WriterService{
    Writer findWriterById(Long id);
}
