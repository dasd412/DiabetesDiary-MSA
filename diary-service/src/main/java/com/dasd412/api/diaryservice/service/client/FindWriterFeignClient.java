package com.dasd412.api.diaryservice.service.client;

import com.dasd412.api.diaryservice.controller.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("writer-service")
public interface FindWriterFeignClient {

    @RequestMapping(method= RequestMethod.GET,value="/writer/{writerId}",consumes = "application/json")
    Long findWriterById(@PathVariable("writerId")Long writerId);

}
