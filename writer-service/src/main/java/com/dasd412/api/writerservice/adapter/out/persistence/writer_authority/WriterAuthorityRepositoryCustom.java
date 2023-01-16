package com.dasd412.api.writerservice.adapter.out.persistence.writer_authority;

import com.dasd412.api.writerservice.domain.authority.WriterAuthority;

import java.util.List;

public interface WriterAuthorityRepositoryCustom {

    List<WriterAuthority> findAllWriterAuthority(Long writerId);

    void deleteWriterAuthorityInIds(List<Long>ids);
}
