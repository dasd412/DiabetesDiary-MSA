package com.dasd412.api.diaryservice.application.service.impl;

import com.dasd412.api.diaryservice.adapter.out.client.FindWriterFeignClient;
import com.dasd412.api.diaryservice.application.service.SaveDiaryService;
import com.dasd412.api.diaryservice.adapter.in.web.dto.post.DiaryPostRequestDTO;
import com.dasd412.api.diaryservice.domain.diary.DiabetesDiary;
import com.dasd412.api.diaryservice.adapter.out.persistence.diary.DiaryRepository;
import com.dasd412.api.diaryservice.domain.diet.Diet;
import com.dasd412.api.diaryservice.domain.food.Food;
import com.dasd412.api.diaryservice.adapter.out.message.ActionEum;
import com.dasd412.api.diaryservice.adapter.out.message.source.KafkaSourceBean;
import com.dasd412.api.diaryservice.common.utils.date.DateStringJoiner;
import com.dasd412.api.diaryservice.common.utils.trace.UserContextHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

@SuppressWarnings({"unused","static-access"})
@Service
public class SaveDiaryServiceImpl implements SaveDiaryService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DiaryRepository diaryRepository;

    private final FindWriterFeignClient findWriterFeignClient;

    private final KafkaSourceBean kafkaSourceBean;

    public SaveDiaryServiceImpl(DiaryRepository diaryRepository, FindWriterFeignClient findWriterFeignClient, KafkaSourceBean kafkaSourceBean) {
        this.diaryRepository = diaryRepository;
        this.findWriterFeignClient = findWriterFeignClient;
        this.kafkaSourceBean = kafkaSourceBean;
    }

    //todo JWT 도입 이후 feignClient 부분은 지울 필요 있을지도... 그리고 트랜잭션 처리는 어떻게 해야할까?
    @Transactional
    public Long postDiaryWithEntities(DiaryPostRequestDTO dto) throws TimeoutException {
        logger.info("call writer micro service for finding writer id. correlation id :{}", UserContextHolder.getContext().getCorrelationId());

        Long writerId = findWriterFeignClient.findWriterById(dto.getWriterId());

        LocalDateTime writtenTime = convertStringToLocalDateTime(dto);

        Long diaryId = makeDiaryWithSubEntities(writerId, dto, writtenTime);

        sendMessageToWriterService(dto.getWriterId(),diaryId);

        sendMessageToFindDiaryService();

        return diaryId;
    }

    private Long makeDiaryWithSubEntities(Long writerId, DiaryPostRequestDTO dto, LocalDateTime writtenTime) throws TimeoutException {
        logger.info("saving diary in SaveDiaryService correlation id :{}", UserContextHolder.getContext().getCorrelationId());
        DiabetesDiary diary = new DiabetesDiary(writerId, dto.getFastingPlasmaGlucose(), dto.getRemark(), writtenTime);

        if (dto.getDietList() != null) {

            dto.getDietList().forEach(
                    dietDTO -> {
                        Diet diet = new Diet(diary, dietDTO.getEatTime(), dietDTO.getBloodSugar());
                        diary.addDiet(diet);

                        if (dietDTO.getFoodList() != null) {
                            dietDTO.getFoodList().forEach(
                                    foodDto -> {
                                        Food food = new Food(diet, foodDto.getFoodName(), foodDto.getAmount());
                                        diet.addFood(food);
                                    }
                            );
                        }
                    });
        }

        diaryRepository.save(diary);

        return diary.getId();
    }

    private void sendMessageToWriterService(Long writerId,Long diaryId) throws TimeoutException  {
        logger.info("diary-service sent message to writer-service in SaveDiaryService. correlation id :{}", UserContextHolder.getContext().getCorrelationId());
        kafkaSourceBean.publishDiaryChangeToWriter(ActionEum.CREATED,writerId,diaryId);
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
    private LocalDateTime convertStringToLocalDateTime(DiaryPostRequestDTO dto) {
        DateStringJoiner dateStringJoiner = DateStringJoiner.builder()
                .year(dto.getYear()).month(dto.getMonth()).day(dto.getDay())
                .hour(dto.getHour()).minute(dto.getMinute()).second(dto.getSecond())
                .build();

        return dateStringJoiner.convertLocalDateTime();
    }
}
