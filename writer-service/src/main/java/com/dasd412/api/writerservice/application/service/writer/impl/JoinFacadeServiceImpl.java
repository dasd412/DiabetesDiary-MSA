package com.dasd412.api.writerservice.application.service.writer.impl;

import com.dasd412.api.writerservice.adapter.out.web.exception.EmailExistException;
import com.dasd412.api.writerservice.adapter.out.web.exception.UserNameExistException;
import com.dasd412.api.writerservice.application.service.authority.AuthorityService;
import com.dasd412.api.writerservice.application.service.security.vo.UserDetailsVO;
import com.dasd412.api.writerservice.application.service.writer.JoinFacadeService;
import com.dasd412.api.writerservice.application.service.writer.SaveWriterService;
import com.dasd412.api.writerservice.application.service.writerauthority.WriterAuthorityService;
import com.dasd412.api.writerservice.common.utils.UserContextHolder;
import com.dasd412.api.writerservice.domain.authority.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

@Service
public class JoinFacadeServiceImpl implements JoinFacadeService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SaveWriterService saveWriterService;

    private final AuthorityService authorityService;

    private final WriterAuthorityService writerAuthorityService;

    public JoinFacadeServiceImpl(SaveWriterService saveWriterService, AuthorityService authorityService, WriterAuthorityService writerAuthorityService) {
        this.saveWriterService = saveWriterService;
        this.authorityService = authorityService;
        this.writerAuthorityService = writerAuthorityService;
    }

    @Override
    public void join(UserDetailsVO vo, Set<Role> roleSet) throws TimeoutException,UserNameExistException, EmailExistException {
        logger.info("join in JoinFacadeService:{}", UserContextHolder.getContext().getCorrelationId());

        //회원 가입
        Long writerId = saveWriterService.saveWriter(vo);

        //권한들 저장
        List<Long> authorityIds = authorityService.createAuthority(roleSet);

        //작성자와 권한들 연관 관계 맺기
        for (Long authorityId : authorityIds) {
            writerAuthorityService.createWriterAuthority(writerId, authorityId);
        }
    }
}
