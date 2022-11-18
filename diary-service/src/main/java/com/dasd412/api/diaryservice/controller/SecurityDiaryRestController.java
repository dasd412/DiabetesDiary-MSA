package com.dasd412.api.diaryservice.controller;

import com.dasd412.api.diaryservice.controller.dto.SecurityDiaryPostRequestDTO;
import com.dasd412.api.diaryservice.controller.dto.SecurityDiaryPostResponseDTO;
import com.dasd412.api.diaryservice.service.SaveDiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/diary/user/diabetes-diary")
public class SecurityDiaryRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SaveDiaryService saveDiaryService;

    public SecurityDiaryRestController(SaveDiaryService saveDiaryService) {
        this.saveDiaryService = saveDiaryService;
    }

    @PostMapping
    public ApiResult<SecurityDiaryPostResponseDTO> postDiary(@RequestBody @Valid SecurityDiaryPostRequestDTO dto) {
        logger.info("post diary with authenticated user");

        //todo 시큐리티 적용한 것으로 바꿔야할지 고민해봐야 함.
        //Long diaryId = saveDiaryService.postDiaryWithEntities(principalDetails, dto);

        Long diaryId = saveDiaryService.postDiaryWithEntities(dto);

        return ApiResult.OK(new SecurityDiaryPostResponseDTO(diaryId));
    }
}
