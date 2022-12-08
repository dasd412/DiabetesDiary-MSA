package com.dasd412.api.readdiaryservice.service;

import com.dasd412.api.readdiaryservice.utils.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReadDiaryService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //todo 나중에 시그니쳐 제대로 바꾸자.
    @Transactional(readOnly = true)
    public String findByWriterId() {
        logger.info("reading diary by writer id in ReadDiaryService correlation id :{}", UserContextHolder.getContext().getCorrelationId());
        return "test find code";
    }
}
