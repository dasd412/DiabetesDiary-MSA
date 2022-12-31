package com.dasd412.api.writerservice.application.service.vo;

import com.dasd412.api.writerservice.domain.authority.Role;
import com.dasd412.api.writerservice.domain.authority.WriterAuthority;
import com.dasd412.api.writerservice.domain.writer.Writer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Set;

public interface AuthenticationVO {

    Writer makeEntityWithPasswordEncode(BCryptPasswordEncoder bCryptPasswordEncoder);

    String getName();

    String getEmail();

    String getProvider();

    String getProviderId();

    Set<WriterAuthority> getWriterAuthorities();
}

