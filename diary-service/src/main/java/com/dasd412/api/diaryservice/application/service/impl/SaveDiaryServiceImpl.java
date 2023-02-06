package com.dasd412.api.diaryservice.application.service.impl;

import com.dasd412.api.diaryservice.adapter.out.client.FindWriterFeignClient;
import com.dasd412.api.diaryservice.adapter.out.message.model.readdiary.dto.DiaryToReaderDTO;
import com.dasd412.api.diaryservice.adapter.out.message.model.readdiary.dto.DietToReaderDTO;
import com.dasd412.api.diaryservice.adapter.out.message.model.readdiary.dto.FoodToReaderDTO;
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
import java.util.ArrayList;
import java.util.List;
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
    public DiaryToReaderDTO postDiaryWithEntities(Long writerId, DiaryPostRequestDTO dto) throws TimeoutException {
        logger.info("call writer micro service for finding writer id. correlation id :{}", UserContextHolder.getContext().getCorrelationId());

        LocalDateTime writtenTime = convertStringToLocalDateTime(dto);

        // 1. 실제 엔티티 저장
        DiabetesDiary diary = new DiabetesDiary(writerId, dto.getFastingPlasmaGlucose(), dto.getRemark(), writtenTime);

        if (dto.getDietList() != null) {

            dto.getDietList().forEach(
                    dietDTO -> {
                        Diet diet = new Diet(diary, dietDTO.getEatTime(), dietDTO.getBloodSugar());
                        diary.addDiet(diet);

                        if (dietDTO.getFoodList() != null) {
                            dietDTO.getFoodList().forEach(
                                    foodDto -> {
                                        Food food = new Food(diet, foodDto.getFoodName(), foodDto.getAmount(), foodDto.getAmountUnit());
                                        diet.addFood(food);
                                    }
                            );
                        }
                    });

        }

        // save를 해야 채번이 된다.
        diaryRepository.save(diary);

        // 2. 트랜잭션 내에 영속성 컨텍스트가 살아있으므로 여기서 dto를 만들어준다. 안그러면 읽기를 위해 한 번 더 디스크를 긁어야 한다.
        DiaryToReaderDTO diaryToReaderDTO = new DiaryToReaderDTO(diary);

        if (diary.getDietList() != null) {

            List<DietToReaderDTO> dietToReaderDTOList = new ArrayList<>();

            diary.getDietList().forEach(
                    diet -> {

                        List<FoodToReaderDTO> foodToReaderDTOList = new ArrayList<>();

                        if (diet.getFoodList() != null) {
                            diet.getFoodList().forEach(
                                    food -> {
                                        foodToReaderDTOList.add(new FoodToReaderDTO(food, diet.getDietId()));
                                    }
                            );
                        }
                        dietToReaderDTOList.add(new DietToReaderDTO(diet, diary.getDiaryId(), foodToReaderDTOList));
                    });

            diaryToReaderDTO.addDietList(dietToReaderDTOList);
        }

        return diaryToReaderDTO;
    }

    public void sendMessageToWriterService(Long writerId, Long diaryId) throws TimeoutException {
        logger.info("diary-service sent message to writer-service in SaveDiaryService. correlation id :{}", UserContextHolder.getContext().getCorrelationId());
        kafkaSourceBean.publishDiaryChangeToWriter(ActionEnum.CREATED, writerId, diaryId);
    }

    public void sendMessageToFindDiaryService(DiaryToReaderDTO dto) {
        logger.info("diary-service sent message to find-diary-service in SaveDiaryService. correlation id :{}", UserContextHolder.getContext().getCorrelationId());
        kafkaSourceBean.publishDiaryChangeToReadDiary(ActionEnum.CREATED,dto);
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
