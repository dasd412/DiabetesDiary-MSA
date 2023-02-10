package com.dasd412.api.diaryservice.application.service.impl;

import com.dasd412.api.diaryservice.adapter.in.web.dto.update.DiaryUpdateRequestDTO;
import com.dasd412.api.diaryservice.adapter.out.client.FindWriterFeignClient;
import com.dasd412.api.diaryservice.adapter.out.message.ActionEnum;
import com.dasd412.api.diaryservice.adapter.out.message.model.readdiary.dto.DiaryToReaderDTO;
import com.dasd412.api.diaryservice.adapter.out.message.model.readdiary.dto.DietToReaderDTO;
import com.dasd412.api.diaryservice.adapter.out.message.model.readdiary.dto.FoodToReaderDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@SuppressWarnings({"unused", "static-access"})
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
    public DiaryToReaderDTO updateDiaryWithEntities(Long writerId, DiaryUpdateRequestDTO dto) throws TimeoutException {
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

        DiaryToReaderDTO diaryToReaderDTO = new DiaryToReaderDTO(targetDiary);

        if (targetDiary.getDietList() != null) {

            List<DietToReaderDTO> dietToReaderDTOList = new ArrayList<>();

            targetDiary.getDietList().forEach(
                    diet -> {

                        List<FoodToReaderDTO> foodToReaderDTOList = new ArrayList<>();

                        if (diet.getFoodList() != null) {
                            diet.getFoodList().forEach(
                                    food -> foodToReaderDTOList.add(new FoodToReaderDTO(food, diet.getDietId()))
                            );
                        }
                        dietToReaderDTOList.add(new DietToReaderDTO(diet, targetDiary.getDiaryId(), foodToReaderDTOList));
                    });

            diaryToReaderDTO.addDietList(dietToReaderDTOList);
        }

        return diaryToReaderDTO;
    }

    public void sendMessageToFindDiaryService(DiaryToReaderDTO dto) {
        logger.info("diary-service sent message to find-diary-service in UpdateDiaryService. correlation id :{}", UserContextHolder.getContext().getCorrelationId());
        kafkaSourceBean.publishDiaryChangeToReadDiary(ActionEnum.UPDATED, dto);
    }
}
