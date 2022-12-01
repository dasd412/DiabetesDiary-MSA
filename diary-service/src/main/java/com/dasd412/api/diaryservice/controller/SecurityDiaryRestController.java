package com.dasd412.api.diaryservice.controller;

import com.dasd412.api.diaryservice.controller.dto.SecurityDiaryPostRequestDTO;
import com.dasd412.api.diaryservice.controller.dto.SecurityDiaryPostResponseDTO;
import com.dasd412.api.diaryservice.service.SaveDiaryService;
import com.dasd412.api.diaryservice.utils.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/diary/user/diabetes-diary")
public class SecurityDiaryRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SaveDiaryService saveDiaryService;

    public SecurityDiaryRestController(SaveDiaryService saveDiaryService) {
        this.saveDiaryService = saveDiaryService;
    }

    @PostMapping
    public ApiResult<?> postDiary(@RequestBody @Valid SecurityDiaryPostRequestDTO dto) {
        logger.info("post diary with authenticated user");
        logger.debug("SecurityDiaryRestController correlation id :{}", UserContextHolder.getContext().getCorrelationId());

        Long diaryId;
        try {
            diaryId = saveDiaryService.postDiaryWithEntities(dto);
            return ApiResult.OK(new SecurityDiaryPostResponseDTO(diaryId));
        } catch (TimeoutException e) {
            return ApiResult.ERROR("Failed to save diary.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
