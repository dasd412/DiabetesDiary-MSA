package com.dasd412.api.writerservice.application.service.security;

import com.dasd412.api.writerservice.adapter.in.security.PrincipalDetails;
import com.dasd412.api.writerservice.adapter.out.persistence.writer.WriterRepository;
import com.dasd412.api.writerservice.domain.writer.Writer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrincipalDetailsService implements UserDetailsService {

    private final WriterRepository writerRepository;

    public PrincipalDetailsService(WriterRepository writerRepository) {
        this.writerRepository = writerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Writer user = writerRepository.findWriterByName(username).orElseThrow(() -> new UsernameNotFoundException("username not exist"));

        return new PrincipalDetails(user);
    }
}
