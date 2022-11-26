package com.dasd412.api.writerservice.domain.writer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WriterRepository extends JpaRepository<Writer, Long>, WriterRepositoryCustom {
}
