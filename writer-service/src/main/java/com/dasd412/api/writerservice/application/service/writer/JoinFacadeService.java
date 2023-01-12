package com.dasd412.api.writerservice.application.service.writer;

import com.dasd412.api.writerservice.adapter.out.web.exception.EmailExistException;
import com.dasd412.api.writerservice.adapter.out.web.exception.UserNameExistException;
import com.dasd412.api.writerservice.application.service.security.vo.UserDetailsVO;
import com.dasd412.api.writerservice.domain.authority.Role;

import java.util.Set;
import java.util.concurrent.TimeoutException;

public interface JoinFacadeService {

    void join(UserDetailsVO vo, Set<Role> roleSet) throws TimeoutException, UserNameExistException, EmailExistException;
}
