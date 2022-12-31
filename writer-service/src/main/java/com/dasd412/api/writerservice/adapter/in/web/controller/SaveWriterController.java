package com.dasd412.api.writerservice.adapter.in.web.controller;

import com.dasd412.api.writerservice.application.service.writer.SaveWriterService;
import com.dasd412.api.writerservice.application.service.writer.impl.SaveWriterServiceImpl;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class SaveWriterController {

    private final SaveWriterService saveWriterService;

    public SaveWriterController(SaveWriterServiceImpl saveWriterService) {
        this.saveWriterService = saveWriterService;
    }

}
