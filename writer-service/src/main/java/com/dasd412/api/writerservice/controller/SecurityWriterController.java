package com.dasd412.api.writerservice.controller;

import com.dasd412.api.writerservice.controller.dto.SaveTestDTO;
import com.dasd412.api.writerservice.domain.writer.Writer;
import com.dasd412.api.writerservice.service.FindWriterService;
import com.dasd412.api.writerservice.service.SaveWriterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SecurityWriterController {

    private final SaveWriterService saveWriterService;
    private final FindWriterService findWriterService;

    public SecurityWriterController(SaveWriterService saveWriterService, FindWriterService findWriterService) {
        this.saveWriterService = saveWriterService;
        this.findWriterService = findWriterService;
    }

    //todo 나중에 지울 메서드
    @PostMapping("/writer/save")
    public ApiResult<Long> saveWriterTest(@RequestBody SaveTestDTO dto) {
        Long writerId = saveWriterService.saveWriterInTest(dto.getName(), dto.getEmail());
        return ApiResult.OK(writerId);
    }

    //todo 나중에 지울 메서드
    @GetMapping("/writer/{writerId}")
    public ResponseEntity<Long> findWriterTest(@PathVariable("writerId") Long writerId) {
        Writer found = findWriterService.findWriterById(writerId);
        return ResponseEntity.ok(found.getId());
    }
}
