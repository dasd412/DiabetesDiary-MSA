package com.dasd412.api.readdiaryservice.adapter.out.persistence.diary;

import com.dasd412.api.readdiaryservice.domain.diary.DiabetesDiaryDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DiaryDocumentRepository extends MongoRepository<DiabetesDiaryDocument,Long> {

}
