package com.dasd412.api.readdiaryservice.adapter.out.web.controller;

import com.dasd412.api.readdiaryservice.adapter.in.web.FoodPageVO;
import com.dasd412.api.readdiaryservice.adapter.out.web.ApiResult;
import com.dasd412.api.readdiaryservice.application.service.ReadDiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DocumentFindController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ReadDiaryService readDiaryService;

    public DocumentFindController(ReadDiaryService readDiaryService) {
        this.readDiaryService = readDiaryService;
    }

    @GetMapping("/fpg/all")
    public ApiResult<?> findAllFpg(@RequestHeader(value = "writer-id") String writerId) {
        return null;
    }

    @GetMapping("/fpg/between")
    public ApiResult<?> finalFpgBetween(@RequestHeader(value = "writer-id") String writerId, @RequestParam
    Map<String, String> timeSpan) {
        return null;
    }

    @GetMapping("/blood-sugar/all")
    public ApiResult<?> findAllBloodSugar(@RequestHeader(value = "writer-id") String writerId) {
        return null;
    }

    @GetMapping("/blood-sugar/between")
    public ApiResult<?> findBloodSugarBetween(@RequestHeader(value = "writer-id") String writerId, @RequestParam
    Map<String, String> timeSpan) {
        return null;
    }

    @GetMapping("/food/list")
    public ApiResult<?> findFoodList(@RequestHeader(value = "writer-id") String writerId, FoodPageVO foodPageVO) {
        return null;
    }
}
