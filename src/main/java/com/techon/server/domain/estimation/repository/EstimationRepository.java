package com.techon.server.domain.estimation.repository;

import com.techon.server.domain.estimation.entity.Category;
import com.techon.server.domain.estimation.entity.Estimation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstimationRepository extends JpaRepository<Estimation, Long> {
    List<Estimation> findByCategoryOrderByPriorityDescIdAsc(Category category);
}
