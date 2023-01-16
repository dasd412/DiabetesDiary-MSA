package com.dasd412.api.writerservice.application.service.writer.impl;

import com.dasd412.api.writerservice.application.service.writer.FindWriterService;
import com.dasd412.api.writerservice.common.utils.UserContextHolder;
import com.dasd412.api.writerservice.domain.writer.Writer;
import com.dasd412.api.writerservice.adapter.out.persistence.writer.WriterRepository;
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
    public Writer findWriterById(Long id) {
        logger.info("find writer by writer id : {}", UserContextHolder.getContext().getCorrelationId());
        return writerRepository.findById(id).orElseThrow(NoResultException::new);
    }

    @Transactional(readOnly = true)
    public Writer findWriterByUsername(String username) {
        logger.info("find writer by writer username : {}", UserContextHolder.getContext().getCorrelationId());
        return writerRepository.findWriterByName(username).orElseThrow(NoResultException::new);
    }
}
