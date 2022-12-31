package com.dasd412.api.writerservice.adapter.in.security.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor
public class LoginRequestDTO {
    @NotBlank
    private final String email;

    @NotBlank
    private final String password;
}
