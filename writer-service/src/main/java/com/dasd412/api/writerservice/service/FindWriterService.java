package com.dasd412.api.writerservice.service;

import com.dasd412.api.writerservice.domain.writer.Writer;
import com.dasd412.api.writerservice.domain.writer.WriterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;

@Service
public class FindWriterService {

    private final WriterRepository writerRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public FindWriterService(WriterRepository writerRepository) {
        this.writerRepository = writerRepository;
    }

    @Transactional(readOnly = true)
    public Writer findWriterById(Long id){
        logger.info("find writer test");
        return writerRepository.findById(id).orElseThrow(NoResultException::new);
    }

}
