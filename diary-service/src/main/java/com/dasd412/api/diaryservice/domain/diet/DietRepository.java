package com.dasd412.api.diaryservice.domain.diet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DietRepository extends JpaRepository<Diet, Long>,DietRepositoryCustom {
}
