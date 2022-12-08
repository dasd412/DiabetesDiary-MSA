package com.dasd412.api.diaryservice.service;

import com.dasd412.api.diaryservice.controller.dto.DiaryPostRequestDTO;
import com.dasd412.api.diaryservice.domain.diary.DiabetesDiary;
import com.dasd412.api.diaryservice.domain.diary.DiaryRepository;
import com.dasd412.api.diaryservice.domain.diet.Diet;
import com.dasd412.api.diaryservice.domain.food.Food;
import com.dasd412.api.diaryservice.service.client.FindWriterFeignClient;
import com.dasd412.api.diaryservice.utils.date.DateStringJoiner;
import com.dasd412.api.diaryservice.utils.trace.UserContextHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

@Service
public class SaveDiaryService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DiaryRepository diaryRepository;

    private final FindWriterFeignClient findWriterFeignClient;

    public SaveDiaryService(DiaryRepository diaryRepository, FindWriterFeignClient findWriterFeignClient) {
        this.diaryRepository = diaryRepository;
        this.findWriterFeignClient = findWriterFeignClient;
    }

    //todo JWT 도입 이후 feignClient 부분은 지울 필요 있을지도... 그리고 트랜잭션 처리는 어떻게 해야할까?
    public Long postDiaryWithEntities(DiaryPostRequestDTO dto) throws TimeoutException {
        logger.info("call writer micro service for finding writer id. correlation id :{}", UserContextHolder.getContext().getCorrelationId());

        Long writerId = findWriterFeignClient.findWriterById(dto.getWriterId());

        LocalDateTime writtenTime = convertStringToLocalDateTime(dto);

        Long diaryId = makeDiaryWithSubEntities(writerId, dto, writtenTime);

        sendMessageToWriterService();

        sendMessageToFindDiaryService();

        return diaryId;
    }


    @Transactional
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
    private LocalDateTime convertStringToLocalDateTime(DiaryPostRequestDTO dto) {
        DateStringJoiner dateStringJoiner = DateStringJoiner.builder()
                .year(dto.getYear()).month(dto.getMonth()).day(dto.getDay())
                .hour(dto.getHour()).minute(dto.getMinute()).second(dto.getSecond())
                .build();

        return dateStringJoiner.convertLocalDateTime();
    }

}
