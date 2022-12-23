package com.dasd412.api.diaryservice.adapter.out.persistence.diary;

import com.dasd412.api.diaryservice.domain.diary.DiabetesDiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryRepository extends JpaRepository<DiabetesDiary, Long>, DiaryRepositoryCustom {
}
