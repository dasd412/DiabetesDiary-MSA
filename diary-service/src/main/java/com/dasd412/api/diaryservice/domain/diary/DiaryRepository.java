package com.dasd412.api.diaryservice.domain.diary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryRepository extends JpaRepository<DiabetesDiary, Long>, DiaryRepositoryCustom {
}
