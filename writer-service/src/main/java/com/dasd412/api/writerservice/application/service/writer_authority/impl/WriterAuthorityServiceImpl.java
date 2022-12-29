package com.dasd412.api.writerservice.application.service.writer_authority.impl;

import com.dasd412.api.writerservice.adapter.out.persistence.authority.AuthorityRepository;
import com.dasd412.api.writerservice.adapter.out.persistence.writer.WriterRepository;
import com.dasd412.api.writerservice.adapter.out.persistence.writer_authority.WriterAuthorityRepository;
import com.dasd412.api.writerservice.application.service.writer_authority.WriterAuthorityService;
import com.dasd412.api.writerservice.common.utils.UserContextHolder;
import com.dasd412.api.writerservice.domain.authority.Authority;
import com.dasd412.api.writerservice.domain.authority.WriterAuthority;
import com.dasd412.api.writerservice.domain.writer.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.concurrent.TimeoutException;


@SuppressWarnings({"static-access"})
@Service
public class WriterAuthorityServiceImpl implements WriterAuthorityService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WriterRepository writerRepository;

    private final AuthorityRepository authorityRepository;

    private final WriterAuthorityRepository writerAuthorityRepository;

    public WriterAuthorityServiceImpl(WriterRepository writerRepository, AuthorityRepository authorityRepository, WriterAuthorityRepository writerAuthorityRepository) {
        this.writerRepository = writerRepository;
        this.authorityRepository = authorityRepository;
        this.writerAuthorityRepository = writerAuthorityRepository;
    }

    @Override
    @Transactional
    public void createWriterAuthority(Long writerId, Long authorityId) throws TimeoutException {
        logger.info("create relation with writer and authority in WriterAuthorityService correlation id :{}", UserContextHolder.getContext().getCorrelationId());

        Writer writer = writerRepository.findById(writerId).orElseThrow(() -> new NoResultException("writer not exist"));

        Authority authority = authorityRepository.findById(authorityId).orElseThrow(() -> new NoResultException("authority not exist"));

        WriterAuthority writerAuthority = new WriterAuthority(writer, authority);

        writer.addWriterAuthority(writerAuthority);

        authority.addWriterAuthority(writerAuthority);

        writerAuthorityRepository.save(writerAuthority);
    }
}
