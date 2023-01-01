package com.dasd412.api.writerservice.adapter.in.web.controller;

import com.dasd412.api.writerservice.adapter.in.security.dto.UserJoinRequestDTO;
import com.dasd412.api.writerservice.adapter.out.web.ApiResult;
import com.dasd412.api.writerservice.adapter.out.web.exception.EmailExistException;
import com.dasd412.api.writerservice.adapter.out.web.exception.UserNameExistException;
import com.dasd412.api.writerservice.application.service.authority.AuthorityService;
import com.dasd412.api.writerservice.application.service.vo.UserDetailsVO;
import com.dasd412.api.writerservice.application.service.writer.SaveWriterService;
import com.dasd412.api.writerservice.application.service.writerauthority.WriterAuthorityService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.TimeoutException;

@RestController
public class JoinController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SaveWriterService saveWriterService;

    private final AuthorityService authorityService;

    private final WriterAuthorityService writerAuthorityService;

    public JoinController(SaveWriterService saveWriterService, AuthorityService authorityService, WriterAuthorityService writerAuthorityService) {
        this.saveWriterService = saveWriterService;
        this.authorityService = authorityService;
        this.writerAuthorityService = writerAuthorityService;
    }

    @PostMapping("/signup")
    public ApiResult<?> signup(@RequestBody @Valid UserJoinRequestDTO dto) throws TimeoutException {

        UserDetailsVO userDetailsVO = UserDetailsVO.builder()
                .name(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .build();

        //1.dto 내 작성자 저장.
        try {
            Long writerId = saveWriterService.saveWriter(userDetailsVO);

            //2.dto 내 권한들 저장
            List<Long> authorityIds = authorityService.createAuthority(dto.getRoles());

            //3. 작성자와 권한들 연관 관계 맺기
            for (Long authorityId : authorityIds) {
                writerAuthorityService.createWriterAuthority(writerId, authorityId);
            }
            return ApiResult.OK("join success");
        } catch (UserNameExistException | EmailExistException e) {
            return ApiResult.ERROR(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
