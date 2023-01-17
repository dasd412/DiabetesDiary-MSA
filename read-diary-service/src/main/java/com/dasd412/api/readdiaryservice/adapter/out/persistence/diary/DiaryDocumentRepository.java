package com.dasd412.api.readdiaryservice.adapter.out.persistence.diary;

import com.dasd412.api.readdiaryservice.domain.diary.DiabetesDiaryDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryDocumentRepository extends MongoRepository<DiabetesDiaryDocument,Long>,DiaryDocumentRepositoryCustom {

}
