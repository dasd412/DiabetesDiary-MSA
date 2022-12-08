package com.dasd412.api.readdiaryservice.controller;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.http.HttpStatus;

public class ApiResult<T> {

    private final boolean success;

    /**
     * 이 클래스가 감쌀 객체.
     */
    private final T response;

    /**
     * 에러가 발생했을 경우 담을 객체.
     */
    private final ApiError error;

    private ApiResult(boolean success, T response, ApiError error) {
        this.success = success;
        this.response = response;
        this.error = error;
    }

    /**
     * RestController 메서드 성공시 사용된다.
     */
    public static <T> ApiResult<T> OK(T response) {
        return new ApiResult<>(true, response, null);
    }

    /**
     * RestController 메서드 실패 시 예외 내용을 알려주기 위해 사용된다.
     */
    public static ApiResult<?> ERROR(Throwable throwable, HttpStatus status) {
        return new ApiResult<>(false, null, new ApiError(throwable, status));
    }

    public static ApiResult<?> ERROR(String errorMessage, HttpStatus status) {
        return new ApiResult<>(false, null, new ApiError(errorMessage, status));
    }

    public boolean isSuccess() {
        return success;
    }

    public T getResponse() {
        return response;
    }

    public ApiError getError() {
        return error;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("success", success)
                .append("response", response)
                .append("error", error)
                .toString();
    }
}
