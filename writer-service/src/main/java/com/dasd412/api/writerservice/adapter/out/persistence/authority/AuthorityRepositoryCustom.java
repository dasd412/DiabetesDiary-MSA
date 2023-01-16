package com.dasd412.api.writerservice.adapter.out.persistence.authority;

import com.dasd412.api.writerservice.domain.authority.Authority;

import java.util.List;

public interface AuthorityRepositoryCustom {

    List<Authority> findAllAuthority(long writerId);

    void deleteAuthorityInIds(List<Long>ids);
}
