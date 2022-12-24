package com.dasd412.api.writerservice.application.service.impl;

import com.dasd412.api.writerservice.application.service.FindWriterService;
import com.dasd412.api.writerservice.application.service.WriterService;
import com.dasd412.api.writerservice.domain.writer.Writer;
import com.dasd412.api.writerservice.adapter.out.persistence.WriterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;

@Service
public class FindWriterServiceImpl implements FindWriterService {

    private final WriterRepository writerRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public FindWriterServiceImpl(WriterRepository writerRepository) {
        this.writerRepository = writerRepository;
    }

    @Transactional(readOnly = true)
    public Writer findWriterById(Long id){
        return writerRepository.findById(id).orElseThrow(NoResultException::new);
    }
}
