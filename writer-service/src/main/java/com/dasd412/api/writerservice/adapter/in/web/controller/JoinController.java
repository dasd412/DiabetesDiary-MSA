package com.dasd412.api.writerservice.adapter.in.web.controller;

import com.dasd412.api.writerservice.adapter.in.security.dto.UserJoinRequestDTO;
import com.dasd412.api.writerservice.adapter.out.web.ApiResult;
import com.dasd412.api.writerservice.application.service.writer.SaveWriterService;
import com.dasd412.api.writerservice.application.service.writerauthority.WriterAuthorityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class JoinController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SaveWriterService saveWriterService;

    private final WriterAuthorityService writerAuthorityService;

    public JoinController(SaveWriterService saveWriterService, WriterAuthorityService writerAuthorityService) {
        this.saveWriterService = saveWriterService;
        this.writerAuthorityService = writerAuthorityService;
    }

    @PostMapping("/signup")
    public ApiResult<?> signup(@RequestBody @Valid UserJoinRequestDTO dto) {

        return null;
    }
}
