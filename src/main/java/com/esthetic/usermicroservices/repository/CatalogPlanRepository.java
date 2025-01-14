package com.esthetic.usermicroservices.repository;

import com.esthetic.usermicroservices.entity.CatalogPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogPlanRepository extends JpaRepository<CatalogPlan, Long> {
}
