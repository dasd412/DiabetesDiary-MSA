package com.dasd412.api.writerservice.application.service.writerauthority;

import java.util.concurrent.TimeoutException;

public interface WriterAuthorityService {

    void createWriterAuthority(Long writerId,Long authorityId)throws TimeoutException;
}
