package com.dasd412.api.writerservice.application.service.writer;

import com.dasd412.api.writerservice.domain.writer.Writer;

public interface FindWriterService extends WriterService {
    Writer findWriterById(Long id);

    Writer findWriterByUsername(String username);
}
