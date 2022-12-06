package com.dasd412.api.diaryservice.domain.food;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long>,FoodRepositoryCustom {
}
