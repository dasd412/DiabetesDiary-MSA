package com.dasd412.api.diaryservice.adapter.out.persistence.diet;

import com.dasd412.api.diaryservice.domain.diet.Diet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DietRepository extends JpaRepository<Diet, Long>, DietRepositoryCustom {
}
