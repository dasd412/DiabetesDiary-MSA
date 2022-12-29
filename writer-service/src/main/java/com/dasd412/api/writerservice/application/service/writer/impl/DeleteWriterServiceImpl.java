package com.dasd412.api.writerservice.application.service.writer.impl;

import com.dasd412.api.writerservice.adapter.out.persistence.writer.WriterRepository;
import com.dasd412.api.writerservice.application.service.writer.DeleteWriterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DeleteWriterServiceImpl implements DeleteWriterService {

    private final WriterRepository writerRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DeleteWriterServiceImpl(WriterRepository writerRepository) {
        this.writerRepository = writerRepository;
    }
}
