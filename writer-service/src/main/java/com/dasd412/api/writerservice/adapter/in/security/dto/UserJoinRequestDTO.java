package com.dasd412.api.writerservice.adapter.in.security.dto;

import com.dasd412.api.writerservice.domain.authority.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class UserJoinRequestDTO implements Serializable {

    @NotBlank
    private final String username;

    @NotBlank
    @Email
    private final String email;

    @NotBlank
    private final String password;

    @NotBlank
    private final List<Role> role;
}
