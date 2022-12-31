package com.dasd412.api.writerservice.application.service.authority;

import com.dasd412.api.writerservice.domain.authority.Role;

import java.util.List;
import java.util.concurrent.TimeoutException;

public interface AuthorityService {

    List<Long> createAuthority(List<Role>roles)throws TimeoutException;
}
