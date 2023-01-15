package com.dasd412.api.readdiaryservice.adapter.in.web;

import com.dasd412.api.readdiaryservice.application.service.ReadDiaryService;
import com.dasd412.api.readdiaryservice.application.service.impl.ReadDiaryServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/diabetes-diary")
public class ReadDiaryRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ReadDiaryService diaryService;

    public ReadDiaryRestController(ReadDiaryServiceImpl diaryService) {
        this.diaryService = diaryService;
    }

}
