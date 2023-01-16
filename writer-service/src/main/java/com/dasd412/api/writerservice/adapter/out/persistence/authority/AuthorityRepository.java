package com.dasd412.api.writerservice.adapter.out.persistence.authority;

import com.dasd412.api.writerservice.domain.authority.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long>, AuthorityRepositoryCustom {
}
