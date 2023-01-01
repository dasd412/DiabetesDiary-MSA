package com.dasd412.api.writerservice.application.service.authority;

import com.dasd412.api.writerservice.domain.authority.Role;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

public interface AuthorityService {

    List<Long> createAuthority(Set<Role> roles)throws TimeoutException;
}
