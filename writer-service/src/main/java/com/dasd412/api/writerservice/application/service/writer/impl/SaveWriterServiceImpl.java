package com.dasd412.api.writerservice.application.service.writer.impl;

import com.dasd412.api.writerservice.adapter.out.web.exception.EmailExistException;
import com.dasd412.api.writerservice.adapter.out.web.exception.UserNameExistException;
import com.dasd412.api.writerservice.application.service.vo.AuthenticationVO;
import com.dasd412.api.writerservice.application.service.writer.SaveWriterService;
import com.dasd412.api.writerservice.common.utils.UserContextHolder;
import com.dasd412.api.writerservice.adapter.out.persistence.writer.WriterRepository;

import com.dasd412.api.writerservice.domain.writer.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeoutException;

@Service
public class SaveWriterServiceImpl implements SaveWriterService {

    private final WriterRepository writerRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SaveWriterServiceImpl(WriterRepository writerRepository) {
        this.writerRepository = writerRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    @Override
    public Long saveWriter(AuthenticationVO vo) throws TimeoutException, UserNameExistException, EmailExistException {
        logger.info("create writer in SaveWriterService correlation id :{}", UserContextHolder.getContext().getCorrelationId());

        if (writerRepository.existName(vo.getName())) {
            throw new UserNameExistException("username already exist");
        }

        if (writerRepository.existEmail(vo.getEmail(), vo.getProvider())) {
            throw new EmailExistException("email already exist");
        }

        Writer entity = vo.makeEntityWithPasswordEncode(bCryptPasswordEncoder);

        writerRepository.save(entity);

        return entity.getId();
    }
}
