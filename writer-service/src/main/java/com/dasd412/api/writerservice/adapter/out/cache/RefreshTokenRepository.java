package com.dasd412.api.writerservice.adapter.out.cache;

import com.dasd412.api.writerservice.adapter.in.security.jwt.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

}
