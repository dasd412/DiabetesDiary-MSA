package com.dasd412.api.diaryservice.application.service.impl;

import com.dasd412.api.diaryservice.adapter.in.web.dto.update.DiaryUpdateRequestDTO;
import com.dasd412.api.diaryservice.adapter.out.client.FindWriterFeignClient;
import com.dasd412.api.diaryservice.adapter.out.message.source.KafkaSourceBean;
import com.dasd412.api.diaryservice.adapter.out.persistence.diary.DiaryRepository;
import com.dasd412.api.diaryservice.adapter.out.persistence.diet.DietRepository;
import com.dasd412.api.diaryservice.adapter.out.persistence.food.FoodRepository;
import com.dasd412.api.diaryservice.application.service.UpdateDiaryService;
import com.dasd412.api.diaryservice.common.utils.trace.UserContextHolder;

import com.dasd412.api.diaryservice.domain.diary.DiabetesDiary;
import com.dasd412.api.diaryservice.domain.diet.Diet;
import com.dasd412.api.diaryservice.domain.food.Food;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.concurrent.TimeoutException;

@SuppressWarnings({"unused","static-access"})
@Service
public class UpdateDiaryServiceImpl implements UpdateDiaryService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DiaryRepository diaryRepository;

    private final KafkaSourceBean kafkaSourceBean;

    private final DietRepository dietRepository;

    private final FoodRepository foodRepository;

    public UpdateDiaryServiceImpl(DiaryRepository diaryRepository,
                                  FindWriterFeignClient findWriterFeignClient,
                                  KafkaSourceBean kafkaSourceBean,
                                  DietRepository dietRepository,
                                  FoodRepository foodRepository) {
        this.diaryRepository = diaryRepository;
        this.kafkaSourceBean = kafkaSourceBean;
        this.dietRepository = dietRepository;
        this.foodRepository = foodRepository;
    }

    @Override
    @Transactional
    public Long updateDiaryWithEntities(Long writerId, DiaryUpdateRequestDTO dto) throws TimeoutException {

        Long diaryId = updateDiaryWithSubEntities(writerId, dto);

        sendMessageToFindDiaryService();

        return diaryId;
    }

    private Long updateDiaryWithSubEntities(Long writerId, DiaryUpdateRequestDTO dto) {
        logger.info("updating diary in UpdateDiaryService correlation id :{}", UserContextHolder.getContext().getCorrelationId());

        DiabetesDiary targetDiary = diaryRepository.findById(dto.getDiaryId()).orElseThrow(() -> new NoResultException("diary not exist"));
        targetDiary.update(dto.getFastingPlasmaGlucose(), dto.getRemark());

        if (dto.getDietList() != null) {

            dto.getDietList().forEach(
                    dietDTO -> {
                        Diet targetDiet = dietRepository.findById(dietDTO.getDietId()).orElseThrow(() -> new NoResultException("diet not exist"));
                        targetDiet.update(dietDTO.getEatTime(), dietDTO.getBloodSugar());

                        if (dietDTO.getFoodList() != null) {
                            dietDTO.getFoodList().forEach(
                                    foodDto -> {
                                        Food targetFood = foodRepository.findById(foodDto.getFoodId()).orElseThrow(() -> new NoResultException("food not exist"));
                                        targetFood.update(foodDto.getFoodName(), foodDto.getAmount(), foodDto.getAmountUnit());
                                    });
                        }
                    });
        }

        diaryRepository.save(targetDiary);

        return targetDiary.getId();
    }

    private void sendMessageToFindDiaryService() {
        logger.info("diary-service sent message to find-diary-service in UpdateDiaryService. correlation id :{}", UserContextHolder.getContext().getCorrelationId());
    }
}
