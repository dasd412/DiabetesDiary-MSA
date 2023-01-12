package com.dasd412.api.writerservice.adapter.in.web.controller;

import brave.ScopedSpan;
import brave.Tracer;
import com.dasd412.api.writerservice.adapter.in.web.controller.dto.UserJoinRequestDTO;
import com.dasd412.api.writerservice.adapter.out.web.ApiResult;
import com.dasd412.api.writerservice.adapter.out.web.exception.EmailExistException;
import com.dasd412.api.writerservice.adapter.out.web.exception.UserNameExistException;
import com.dasd412.api.writerservice.application.service.security.vo.UserDetailsVO;
import com.dasd412.api.writerservice.application.service.writer.JoinFacadeService;

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

    private final JoinFacadeService joinFacadeService;

    private final Tracer tracer;

    public JoinController(JoinFacadeService joinFacadeService, Tracer tracer) {
        this.joinFacadeService = joinFacadeService;
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

        try {
            joinFacadeService.join(userDetailsVO, dto.getRoles());

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
