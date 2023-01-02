package com.dasd412.api.writerservice.adapter.in.security.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Getter
@RequiredArgsConstructor
public class LoginRequestDTO implements Serializable {

    @NotBlank
    @Email
    private final String email;

    @NotBlank
    private final String password;
}
