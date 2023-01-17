package com.dasd412.api.readdiaryservice.adapter.out.persistence.diary;

import com.dasd412.api.readdiaryservice.domain.diary.DiabetesDiaryDocument;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


import java.util.List;

public class DiaryDocumentRepositoryImpl implements DiaryDocumentRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public DiaryDocumentRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<DiabetesDiaryDocument> getDiabetesDiariesOfWriter(Long writerId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("writer_id").is(writerId));
        return mongoTemplate.find(query, DiabetesDiaryDocument.class);
    }
}
