package com.dasd412.api.diaryservice.application.service.impl;

import com.dasd412.api.diaryservice.adapter.out.client.FindWriterFeignClient;
import com.dasd412.api.diaryservice.adapter.out.message.ActionEum;
import com.dasd412.api.diaryservice.adapter.out.message.source.KafkaSourceBean;
import com.dasd412.api.diaryservice.adapter.out.persistence.diary.DiaryRepository;
import com.dasd412.api.diaryservice.adapter.out.persistence.diet.DietRepository;
import com.dasd412.api.diaryservice.adapter.out.persistence.food.FoodRepository;
import com.dasd412.api.diaryservice.application.service.DeleteDiaryService;
import com.dasd412.api.diaryservice.common.utils.trace.UserContextHolder;
import com.dasd412.api.diaryservice.domain.diary.DiabetesDiary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.concurrent.TimeoutException;

@Service
public class DeleteDiaryServiceImpl implements DeleteDiaryService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //todo 이 동기식 검증이 JWT 도입 이후에도 필요할지는 고민 필요.
    private final FindWriterFeignClient findWriterFeignClient;

    private final KafkaSourceBean kafkaSourceBean;

    private final DiaryRepository diaryRepository;

    private final DietRepository dietRepository;

    private final FoodRepository foodRepository;

    public DeleteDiaryServiceImpl(FindWriterFeignClient findWriterFeignClient,
                                  KafkaSourceBean kafkaSourceBean,
                                  DiaryRepository diaryRepository,
                                  DietRepository dietRepository,
                                  FoodRepository foodRepository) {
        this.findWriterFeignClient = findWriterFeignClient;
        this.kafkaSourceBean = kafkaSourceBean;
        this.diaryRepository = diaryRepository;
        this.dietRepository = dietRepository;
        this.foodRepository = foodRepository;
    }


    @Override
    public Long deleteDiaryWithSubEntities(Long diaryId) throws TimeoutException {

        DiabetesDiary targetDiary = diaryRepository.findById(diaryId).orElseThrow(() -> new NoResultException("diary not exist"));

        removeDiaryWithSubEntities(diaryId);

        Long writerId = targetDiary.getWriterId();

        sendMessageToWriterService(writerId, diaryId);

        sendMessageToFindDiaryService();

        return diaryId;
    }

    @Transactional
    private void removeDiaryWithSubEntities(Long diaryId) throws TimeoutException {

    }

    private void sendMessageToWriterService(Long writerId, Long diaryId) {
        logger.info("diary-service sent message to writer-service in DeleteDiaryService. correlation id :{}", UserContextHolder.getContext().getCorrelationId());
        kafkaSourceBean.publishDiaryChangeToWriter(ActionEum.DELETED, writerId, diaryId);
    }

    //todo 아파치 카프카 로직 추가 필요
    private void sendMessageToFindDiaryService() {
        logger.info("diary-service sent message to find-diary-service in DeleteDiaryService. correlation id :{}", UserContextHolder.getContext().getCorrelationId());

    }
}
