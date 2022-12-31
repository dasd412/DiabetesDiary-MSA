package com.dasd412.api.writerservice.application.service.authority.impl;

import com.dasd412.api.writerservice.adapter.out.persistence.authority.AuthorityRepository;
import com.dasd412.api.writerservice.application.service.authority.AuthorityService;
import com.dasd412.api.writerservice.common.utils.UserContextHolder;
import com.dasd412.api.writerservice.domain.authority.Authority;
import com.dasd412.api.writerservice.domain.authority.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AuthorityRepository authorityRepository;

    public AuthorityServiceImpl(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @Override
    @Transactional
    public List<Long> createAuthority(List<Role> roles) throws TimeoutException {
        logger.info("create authorities in AuthorityService correlation id :{}", UserContextHolder.getContext().getCorrelationId());

        List<Long> authorityIds = new ArrayList<>();

        roles.forEach(role -> {
            authorityRepository.save(new Authority(role));
        });

        return authorityIds;
    }
}
