package com.dasd412.api.diaryservice.application.service.impl;

import com.dasd412.api.diaryservice.adapter.in.web.dto.delete.DiaryDeleteRequestDTO;
import com.dasd412.api.diaryservice.adapter.out.message.ActionEum;
import com.dasd412.api.diaryservice.adapter.out.message.source.KafkaSourceBean;
import com.dasd412.api.diaryservice.adapter.out.persistence.diary.DiaryRepository;
import com.dasd412.api.diaryservice.adapter.out.persistence.diet.DietRepository;
import com.dasd412.api.diaryservice.adapter.out.persistence.food.FoodRepository;
import com.dasd412.api.diaryservice.application.service.DeleteDiaryService;
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
import java.util.stream.Collectors;

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
    public Long deleteDiaryWithSubEntities(DiaryDeleteRequestDTO dto) throws TimeoutException {

        removeDiaryWithSubEntities(dto.getDiaryId());

        sendMessageToWriterService(dto.getWriterId(), dto.getDiaryId());

        sendMessageToFindDiaryService();

        return dto.getDiaryId();
    }

    @Transactional
    private void removeDiaryWithSubEntities(Long diaryId) throws TimeoutException {
        /*
            DELETE A, B, C
            FROM A
            JOIN B ON A.id = B.a_id
            JOIN C ON B.id = C.b_id
            WHERE A.id = paramId
            을 만족하는 쿼리를 짜야하는데, Querydsl은 기본적으로 delete와 join을 같이 쓰지 못하게 막아놨다.

            cascade.all 했으므로 하위 엔티티도 같이 삭제된다.
        */
        diaryRepository.deleteById(diaryId);
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
