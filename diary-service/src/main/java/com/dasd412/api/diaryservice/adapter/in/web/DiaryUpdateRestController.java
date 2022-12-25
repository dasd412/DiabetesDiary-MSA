package com.dasd412.api.diaryservice.adapter.in.web;

import brave.Tracer;
import com.dasd412.api.diaryservice.adapter.in.web.dto.update.DiaryUpdateRequestDTO;
import com.dasd412.api.diaryservice.adapter.out.web.ApiResult;
import com.dasd412.api.diaryservice.application.service.UpdateDiaryService;
import com.dasd412.api.diaryservice.common.utils.trace.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.concurrent.TimeoutException;

@RestController
public class DiaryUpdateRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UpdateDiaryService updateDiaryService;

    private final Tracer tracer;

    public DiaryUpdateRestController(UpdateDiaryService updateDiaryService, Tracer tracer) {
        this.updateDiaryService = updateDiaryService;
        this.tracer = tracer;
    }

    @PutMapping("/diabetes-diary")
    public ApiResult<?> updateDiary(@RequestBody @Valid DiaryUpdateRequestDTO dto) throws TimeoutException {
        logger.info("correlation id in updating diary of DiaryRestController:{}", UserContextHolder.getContext().getCorrelationId());

        return null;
    }
}
