package com.dasd412.api.writerservice.adapter.in.web;

import com.dasd412.api.writerservice.application.service.writer.FindWriterService;
import com.dasd412.api.writerservice.application.service.writer.impl.FindWriterServiceImpl;
import com.dasd412.api.writerservice.domain.writer.Writer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FindWriterController {

    private final FindWriterService findWriterService;

    public FindWriterController(FindWriterServiceImpl findWriterService) {
        this.findWriterService = findWriterService;
    }

    //todo 나중에 지울 메서드
    @GetMapping("/writer/{writerId}")
    public ResponseEntity<Long> findWriterTest(@PathVariable("writerId") Long writerId) {
        Writer found = findWriterService.findWriterById(writerId);
        return ResponseEntity.ok(found.getId());
    }
}
