package com.dasd412.api.diaryservice.application.service.impl;

import com.dasd412.api.diaryservice.adapter.out.client.FindWriterFeignClient;
import com.dasd412.api.diaryservice.application.service.SaveDiaryService;
import com.dasd412.api.diaryservice.adapter.in.web.dto.post.DiaryPostRequestDTO;
import com.dasd412.api.diaryservice.domain.diary.DiabetesDiary;
import com.dasd412.api.diaryservice.adapter.out.persistence.diary.DiaryRepository;
import com.dasd412.api.diaryservice.domain.diet.Diet;
import com.dasd412.api.diaryservice.domain.food.Food;
import com.dasd412.api.diaryservice.adapter.out.message.ActionEnum;
import com.dasd412.api.diaryservice.adapter.out.message.source.KafkaSourceBean;
import com.dasd412.api.diaryservice.common.utils.date.DateStringJoiner;
import com.dasd412.api.diaryservice.common.utils.trace.UserContextHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

@SuppressWarnings({"unused", "static-access"})
@Service
public class SaveDiaryServiceImpl implements SaveDiaryService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DiaryRepository diaryRepository;

    private final KafkaSourceBean kafkaSourceBean;

    public SaveDiaryServiceImpl(DiaryRepository diaryRepository, FindWriterFeignClient findWriterFeignClient, KafkaSourceBean kafkaSourceBean) {
        this.diaryRepository = diaryRepository;
        this.kafkaSourceBean = kafkaSourceBean;
    }

    @Transactional
    public Long postDiaryWithEntities(Long writerId, DiaryPostRequestDTO dto) throws TimeoutException {
        logger.info("call writer micro service for finding writer id. correlation id :{}", UserContextHolder.getContext().getCorrelationId());

        LocalDateTime writtenTime = convertStringToLocalDateTime(dto);

        Long diaryId = makeDiaryWithSubEntities(writerId, dto, writtenTime);

        sendMessageToWriterService(writerId, diaryId);

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

    private void sendMessageToWriterService(Long writerId, Long diaryId) throws TimeoutException {
        logger.info("diary-service sent message to writer-service in SaveDiaryService. correlation id :{}", UserContextHolder.getContext().getCorrelationId());
        kafkaSourceBean.publishDiaryChangeToWriter(ActionEnum.CREATED, writerId, diaryId);
    }

    //todo ????????? ????????? ?????? ?????? ??????
    private void sendMessageToFindDiaryService() {
        logger.info("diary-service sent message to find-diary-service in SaveDiaryService. correlation id :{}", UserContextHolder.getContext().getCorrelationId());

    }

    /**
     * JSON ???????????? LocalDateTime ?????? ????????? ????????? ????????? ?????? ?????????.
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
