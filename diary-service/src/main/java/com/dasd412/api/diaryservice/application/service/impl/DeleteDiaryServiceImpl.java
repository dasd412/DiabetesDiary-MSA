package com.dasd412.api.diaryservice.application.service.impl;

import com.dasd412.api.diaryservice.adapter.out.message.ActionEum;
import com.dasd412.api.diaryservice.adapter.out.message.source.KafkaSourceBean;
import com.dasd412.api.diaryservice.adapter.out.persistence.diary.DiaryRepository;
import com.dasd412.api.diaryservice.adapter.out.persistence.diet.DietRepository;
import com.dasd412.api.diaryservice.adapter.out.persistence.food.FoodRepository;
import com.dasd412.api.diaryservice.application.service.DeleteDiaryService;
import com.dasd412.api.diaryservice.common.utils.trace.UserContextHolder;

import com.dasd412.api.diaryservice.domain.diet.Diet;
import com.dasd412.api.diaryservice.domain.food.Food;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;


@SuppressWarnings({"unused","static-access"})
@Service
public class DeleteDiaryServiceImpl implements DeleteDiaryService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final KafkaSourceBean kafkaSourceBean;

    private final DiaryRepository diaryRepository;

    private final DietRepository dietRepository;

    private final FoodRepository foodRepository;

    public DeleteDiaryServiceImpl(
            KafkaSourceBean kafkaSourceBean,
            DiaryRepository diaryRepository, DietRepository dietRepository, FoodRepository foodRepository) {
        this.kafkaSourceBean = kafkaSourceBean;
        this.diaryRepository = diaryRepository;
        this.dietRepository = dietRepository;
        this.foodRepository = foodRepository;
    }

    @Override
    @Transactional
    public Long deleteDiaryWithSubEntities(Long diaryId,Long writerId) throws TimeoutException {

        removeDiaryWithSubEntities(diaryId);

        sendMessageToWriterService(writerId, diaryId);

        sendMessageToFindDiaryService();

        return diaryId;
    }

    private void removeDiaryWithSubEntities(Long diaryId) throws TimeoutException {
        List<Diet> targetDiets = dietRepository.findDietsInDiary(diaryId);

        List<Long> dietIds = new ArrayList<>();
        List<Long> foodIds = new ArrayList<>();

        for (Diet diet : targetDiets) {
            dietIds.add(diet.getDietId());
            for (Food food : diet.getFoodList()) {
                foodIds.add(food.getFoodId());
            }
        }
        foodRepository.deleteFoodsInIds(foodIds);
        dietRepository.deleteDietsInIds(dietIds);

        //기본적으로 제공하는 deleteById()쓰면 낙관적락과 관련된 오류가 발생함. 아마 하위 엔티티가 db에서 삭제되고 나서
        //영속성 컨텍스트 내의 엔티티 간 연관 관계 삭제할 때, 이미 없는 것을 삭제하려는 것이기 때문에 문제가 생기나봄...
        diaryRepository.deleteDiaryForBulkDelete(diaryId);
    }

    private void sendMessageToWriterService(Long writerId, Long diaryId) throws TimeoutException  {
        logger.info("diary-service sent message to writer-service in DeleteDiaryService. correlation id :{}", UserContextHolder.getContext().getCorrelationId());
        kafkaSourceBean.publishDiaryChangeToWriter(ActionEum.DELETED, writerId, diaryId);
    }

    //todo 아파치 카프카 로직 추가 필요
    private void sendMessageToFindDiaryService() {
        logger.info("diary-service sent message to find-diary-service in DeleteDiaryService. correlation id :{}", UserContextHolder.getContext().getCorrelationId());

    }
}
