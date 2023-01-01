package com.dasd412.api.writerservice.adapter.in.security.dto;

import com.dasd412.api.writerservice.domain.authority.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

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

    @NotNull
    @Size(min=1)
    private final Set<Role> roles;
}
