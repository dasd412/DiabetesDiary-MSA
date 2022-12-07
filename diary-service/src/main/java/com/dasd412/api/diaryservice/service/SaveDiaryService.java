package com.dasd412.api.diaryservice.service;

import com.dasd412.api.diaryservice.controller.dto.SecurityDiaryPostRequestDTO;
import com.dasd412.api.diaryservice.domain.EntityId;
import com.dasd412.api.diaryservice.domain.diary.DiabetesDiary;
import com.dasd412.api.diaryservice.domain.diary.DiaryRepository;
import com.dasd412.api.diaryservice.domain.diet.Diet;
import com.dasd412.api.diaryservice.domain.diet.DietRepository;
import com.dasd412.api.diaryservice.service.client.FindWriterFeignClient;
import com.dasd412.api.diaryservice.utils.date.DateStringJoiner;
import com.dasd412.api.diaryservice.utils.trace.UserContextHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

@Service
public class SaveDiaryService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DiaryRepository diaryRepository;

    private final DietRepository dietRepository;

    private final FindWriterFeignClient findWriterFeignClient;

    public SaveDiaryService(DiaryRepository diaryRepository, DietRepository dietRepository, FindWriterFeignClient findWriterFeignClient) {
        this.diaryRepository = diaryRepository;
        this.dietRepository = dietRepository;
        this.findWriterFeignClient = findWriterFeignClient;
    }

    //todo JWT 도입 이후 feignClient 부분은 지울 필요 있을지도... 그리고 트랜잭션 처리는 어떻게 해야할까?
    public Long postDiaryWithEntities(SecurityDiaryPostRequestDTO dto) throws TimeoutException {
        logger.info("call writer micro service for finding writer id. correlation id :{}", UserContextHolder.getContext().getCorrelationId());

        Long writerId = findWriterFeignClient.findWriterById(dto.getWriterId());

        LocalDateTime writtenTime = convertStringToLocalDateTime(dto);

        Long diaryId = makeDiaryWithSubEntities(writerId, dto, writtenTime);

        sendMessageToWriterService();

        sendMessageToFindDiaryService();

        return diaryId;
    }

    @Transactional
    Long makeDiaryWithSubEntities(Long writerId, SecurityDiaryPostRequestDTO dto, LocalDateTime writtenTime) throws TimeoutException {
        logger.info("saving diary in SaveDiaryService correlation id :{}", UserContextHolder.getContext().getCorrelationId());
        DiabetesDiary diary = new DiabetesDiary(writerId, dto.getFastingPlasmaGlucose(), dto.getRemark(), writtenTime);
        diaryRepository.save(diary);

        //todo 하위 엔티티 저장도 사이에 넣어야 한다.
        if (dto.getDietList() != null) {

            if (dto.getDietList().size() > 0) {

            }
        }


        return diary.getId();
    }

    //todo 아파치 카프카 로직 추가 필요
    private void sendMessageToWriterService() {
        logger.info("diary-service sent message to writer-service in SaveDiaryService. correlation id :{}", UserContextHolder.getContext().getCorrelationId());

    }

    //todo 아파치 카프카 로직 추가 필요
    private void sendMessageToFindDiaryService() {
        logger.info("diary-service sent message to find-diary-service in SaveDiaryService. correlation id :{}", UserContextHolder.getContext().getCorrelationId());

    }


    /**
     * JSON 직렬화가 LocalDateTime 에는 적용이 안되서 작성한 헬프 메서드.
     *
     * @return String => LocalDateTime
     */
    private LocalDateTime convertStringToLocalDateTime(SecurityDiaryPostRequestDTO dto) {
        DateStringJoiner dateStringJoiner = DateStringJoiner.builder()
                .year(dto.getYear()).month(dto.getMonth()).day(dto.getDay())
                .hour(dto.getHour()).minute(dto.getMinute()).second(dto.getSecond())
                .build();

        return dateStringJoiner.convertLocalDateTime();
    }

}
