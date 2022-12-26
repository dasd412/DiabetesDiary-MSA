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

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.concurrent.TimeoutException;

@Service
public class UpdateDiaryServiceImpl implements UpdateDiaryService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DiaryRepository diaryRepository;

    //todo 이 동기식 검증이 JWT 도입 이후에도 필요할지는 고민 필요.
    private final FindWriterFeignClient findWriterFeignClient;

    private final KafkaSourceBean kafkaSourceBean;

    private final DietRepository dietRepository;

    private final FoodRepository foodRepository;

    public UpdateDiaryServiceImpl(DiaryRepository diaryRepository,
                                  FindWriterFeignClient findWriterFeignClient,
                                  KafkaSourceBean kafkaSourceBean,
                                  DietRepository dietRepository,
                                  FoodRepository foodRepository) {
        this.diaryRepository = diaryRepository;
        this.findWriterFeignClient = findWriterFeignClient;
        this.kafkaSourceBean = kafkaSourceBean;
        this.dietRepository = dietRepository;
        this.foodRepository = foodRepository;
    }

    @Override
    public Long updateDiaryWithEntities(DiaryUpdateRequestDTO dto) throws TimeoutException {

        Long writerId = findWriterFeignClient.findWriterById(dto.getWriterId());

        Long diaryId = updateDiaryWithSubEntities(writerId, dto);

        sendMessageToFindDiaryService();

        return diaryId;
    }

    @Transactional
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
