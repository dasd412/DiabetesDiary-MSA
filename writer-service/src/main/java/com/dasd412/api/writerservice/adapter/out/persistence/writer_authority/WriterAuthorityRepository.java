package com.dasd412.api.writerservice.adapter.out.persistence.writer_authority;

import com.dasd412.api.writerservice.domain.authority.WriterAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WriterAuthorityRepository extends JpaRepository<WriterAuthority, Long>, WriterAuthorityRepositoryCustom {
}
