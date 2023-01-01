package com.dasd412.api.writerservice.adapter.out.persistence.writer;

import com.dasd412.api.writerservice.domain.writer.Writer;

import java.util.Optional;

public interface WriterRepositoryCustom {

    Optional<Writer> findWriterByName(String username);

    Boolean existName(String username);

    Boolean existEmail(String email, String provider);
}
