package com.dasd412.api.writerservice.adapter.in.web;

import com.dasd412.api.writerservice.adapter.out.web.ApiResult;
import com.dasd412.api.writerservice.adapter.out.web.dto.SaveTestDTO;
import com.dasd412.api.writerservice.application.service.writer.SaveWriterService;
import com.dasd412.api.writerservice.application.service.writer.impl.SaveWriterServiceImpl;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SaveWriterController {

    private final SaveWriterService saveWriterService;

    public SaveWriterController(SaveWriterServiceImpl saveWriterService) {
        this.saveWriterService = saveWriterService;
    }

    @PostMapping("/writer/")
    public ApiResult<Long> saveWriterTest(@RequestBody SaveTestDTO dto) {
        Long writerId = saveWriterService.saveWriterInTest(dto.getName(), dto.getEmail());
        return ApiResult.OK(writerId);
    }
}
