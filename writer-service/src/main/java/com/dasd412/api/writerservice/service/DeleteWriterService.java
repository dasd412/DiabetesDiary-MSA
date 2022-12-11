package com.dasd412.api.writerservice.service;

import com.dasd412.api.writerservice.domain.writer.WriterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DeleteWriterService {

    private final WriterRepository writerRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DeleteWriterService(WriterRepository writerRepository) {
        this.writerRepository = writerRepository;
    }
}
