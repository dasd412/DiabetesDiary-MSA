package com.dasd412.api.writerservice.application.service.writer;

import com.dasd412.api.writerservice.adapter.out.web.exception.EmailExistException;
import com.dasd412.api.writerservice.adapter.out.web.exception.UserNameExistException;
import com.dasd412.api.writerservice.application.service.security.vo.AuthenticationVO;
import com.dasd412.api.writerservice.domain.authority.Role;
import com.dasd412.api.writerservice.domain.writer.Writer;

import java.util.Set;
import java.util.concurrent.TimeoutException;

public interface JoinFacadeService {

    Writer join(AuthenticationVO vo, Set<Role> roleSet) throws TimeoutException, UserNameExistException, EmailExistException;
}
