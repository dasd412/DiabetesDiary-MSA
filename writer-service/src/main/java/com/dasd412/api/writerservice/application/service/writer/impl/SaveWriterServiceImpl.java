package com.dasd412.api.writerservice.application.service.writer.impl;

import com.dasd412.api.writerservice.application.service.vo.AuthenticationVO;
import com.dasd412.api.writerservice.application.service.writer.SaveWriterService;
import com.dasd412.api.writerservice.common.utils.UserContextHolder;
import com.dasd412.api.writerservice.domain.writer.Writer;
import com.dasd412.api.writerservice.adapter.out.persistence.writer.WriterRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeoutException;

@Service
public class SaveWriterServiceImpl implements SaveWriterService {

    private final WriterRepository writerRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public SaveWriterServiceImpl(WriterRepository writerRepository) {
        this.writerRepository = writerRepository;
    }

    @Override
    public Long saveWriter(AuthenticationVO vo) throws TimeoutException {
        logger.info("create writer in SaveWriterService correlation id :{}", UserContextHolder.getContext().getCorrelationId());

        return null;
    }
}
