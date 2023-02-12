package com.dasd412.api.writerservice.application.service.writer.impl;

import com.dasd412.api.writerservice.adapter.in.security.JWTTokenProvider;
import com.dasd412.api.writerservice.adapter.in.security.exception.InvalidAccessTokenException;
import com.dasd412.api.writerservice.adapter.out.message.ActionEnum;
import com.dasd412.api.writerservice.adapter.out.message.source.KafkaSourceBean;
import com.dasd412.api.writerservice.adapter.out.persistence.authority.AuthorityRepository;
import com.dasd412.api.writerservice.adapter.out.persistence.writer.WriterRepository;
import com.dasd412.api.writerservice.adapter.out.persistence.writer_authority.WriterAuthorityRepository;
import com.dasd412.api.writerservice.application.service.writer.DeleteWriterService;
import com.dasd412.api.writerservice.common.utils.UserContextHolder;
import com.dasd412.api.writerservice.domain.authority.Authority;
import com.dasd412.api.writerservice.domain.authority.WriterAuthority;
import com.dasd412.api.writerservice.domain.writer.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeleteWriterServiceImpl implements DeleteWriterService {

    private final WriterRepository writerRepository;

    private final AuthorityRepository authorityRepository;

    private final WriterAuthorityRepository writerAuthorityRepository;

    private final JWTTokenProvider jwtTokenProvider;

    private final KafkaSourceBean kafkaSourceBean;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DeleteWriterServiceImpl(WriterRepository writerRepository, AuthorityRepository authorityRepository, WriterAuthorityRepository writerAuthorityRepository, JWTTokenProvider jwtTokenProvider, KafkaSourceBean kafkaSourceBean) {
        this.writerRepository = writerRepository;
        this.authorityRepository = authorityRepository;
        this.writerAuthorityRepository = writerAuthorityRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.kafkaSourceBean = kafkaSourceBean;
    }

    @Override
    @Transactional
    public Long removeWriter(String accessToken) {
        logger.info("removeWriter in DeleteWriterService:{}", UserContextHolder.getContext().getCorrelationId());

        if (!jwtTokenProvider.validateJwtToken(accessToken)) {
            throw new InvalidAccessTokenException("this is invalid access token");
        }

        Long writerId = Long.parseLong(jwtTokenProvider.retrieveWriterId(accessToken));

        Writer found=writerRepository.findById(writerId).orElseThrow(()->new NoResultException("writer not exist"));

        List<WriterAuthority> writerAuthorities = writerAuthorityRepository.findAllWriterAuthority(writerId);

        List<Long> writerAuthorityIds = writerAuthorities.stream().map(WriterAuthority::getId).collect(Collectors.toList());

        List<Authority> authorities = authorityRepository.findAllAuthority(writerId);

        List<Long> authorityIds = authorities.stream().map(Authority::getId).collect(Collectors.toList());

        writerAuthorityRepository.deleteWriterAuthorityInIds(writerAuthorityIds);

        authorityRepository.deleteAuthorityInIds(authorityIds);

        //@ElementCollection 까지 모두 지우기 위해 select 후 delete(entity) 했다.
        writerRepository.delete(found);

        return writerId;
    }

    @Override
    public void sendMessageToOtherService(Long writerId) {
        logger.info("writer-service sent message to  diary-service  in DeleteWriterService. correlation id :{}", UserContextHolder.getContext().getCorrelationId());

        kafkaSourceBean.publishWriterChangeToDiary(ActionEnum.DELETED,writerId);

        kafkaSourceBean.publishWriterChangeToReader(ActionEnum.DELETED,writerId);
    }
}
