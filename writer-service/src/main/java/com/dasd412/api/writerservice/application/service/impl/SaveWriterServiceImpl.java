package com.dasd412.api.writerservice.application.service.impl;

import com.dasd412.api.writerservice.application.service.SaveWriterService;
import com.dasd412.api.writerservice.domain.writer.Writer;
import com.dasd412.api.writerservice.adapter.out.persistence.writer.WriterRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaveWriterServiceImpl implements SaveWriterService {

    private final WriterRepository writerRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public SaveWriterServiceImpl(WriterRepository writerRepository) {
        this.writerRepository = writerRepository;
    }

    //todo 나중에 지울 테스트용 메서드
    @Transactional
    public Long saveWriterInTest(String name, String email){
        logger.info("save test");
        Writer writer=new Writer(name,email);
        writerRepository.save(writer);
        return writer.getId();
    }

}
