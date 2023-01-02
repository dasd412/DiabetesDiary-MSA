package com.dasd412.api.writerservice.application.service.writerauthority;

import com.dasd412.api.writerservice.domain.authority.Authority;

import java.util.List;
import java.util.concurrent.TimeoutException;

public interface WriterAuthorityService {

    void createWriterAuthority(Long writerId, Long authorityId) throws TimeoutException;

    List<Authority> findAllAuthority(long writerId);
}
