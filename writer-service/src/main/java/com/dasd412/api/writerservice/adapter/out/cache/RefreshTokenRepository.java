package com.dasd412.api.writerservice.adapter.out.cache;

import com.dasd412.api.writerservice.adapter.in.security.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

}
