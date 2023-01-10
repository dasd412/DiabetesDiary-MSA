package com.dasd412.api.writerservice.adapter.in.web.controller;

import brave.ScopedSpan;
import brave.Tracer;
import com.dasd412.api.writerservice.adapter.in.web.controller.dto.UserJoinRequestDTO;
import com.dasd412.api.writerservice.adapter.out.web.ApiResult;
import com.dasd412.api.writerservice.adapter.out.web.exception.EmailExistException;
import com.dasd412.api.writerservice.adapter.out.web.exception.UserNameExistException;
import com.dasd412.api.writerservice.application.service.authority.AuthorityService;
import com.dasd412.api.writerservice.application.service.security.vo.UserDetailsVO;
import com.dasd412.api.writerservice.application.service.writer.SaveWriterService;
import com.dasd412.api.writerservice.application.service.writerauthority.WriterAuthorityService;

import com.dasd412.api.writerservice.common.utils.UserContextHolder;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.TimeoutException;

@RestController
public class JoinController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SaveWriterService saveWriterService;

    private final AuthorityService authorityService;

    private final WriterAuthorityService writerAuthorityService;

    private final Tracer tracer;

    public JoinController(SaveWriterService saveWriterService, AuthorityService authorityService, WriterAuthorityService writerAuthorityService, Tracer tracer) {
        this.saveWriterService = saveWriterService;
        this.authorityService = authorityService;
        this.writerAuthorityService = writerAuthorityService;
        this.tracer = tracer;
    }

    @PostMapping("/signup")
    @RateLimiter(name = "writerService")
    @CircuitBreaker(name = "writerService", fallbackMethod = "fallBackSignup")
    public ApiResult<?> signup(@RequestBody @Valid UserJoinRequestDTO dto) throws TimeoutException {
        logger.info("signing up in JoinController:{}", UserContextHolder.getContext().getCorrelationId());

        ScopedSpan span = tracer.startScopedSpan("signup");

        UserDetailsVO userDetailsVO = UserDetailsVO.builder()
                .name(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .build();

        //1.dto 내 작성자 저장.
        try {
            Long writerId = saveWriterService.saveWriter(userDetailsVO);

            //2.dto 내 권한들 저장
            List<Long> authorityIds = authorityService.createAuthority(dto.getRoles());

            //3. 작성자와 권한들 연관 관계 맺기
            for (Long authorityId : authorityIds) {
                writerAuthorityService.createWriterAuthority(writerId, authorityId);
            }
            return ApiResult.OK(HttpStatus.OK);
        } catch (UserNameExistException | EmailExistException e) {
            return ApiResult.ERROR(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.ERROR(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            span.tag("writer.service", "signup");
            span.finish();
        }
    }

    private ApiResult<?> fallBackSignup(UserJoinRequestDTO dto, Throwable throwable) {
        logger.error("error occurred while signing up in JoinController:{}", UserContextHolder.getContext().getCorrelationId());
        if (throwable.getClass().isAssignableFrom(IllegalArgumentException.class)) {
            return ApiResult.ERROR(throwable.getClass().getName(), HttpStatus.BAD_REQUEST);
        }
        return ApiResult.ERROR(throwable.getClass().getName(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
