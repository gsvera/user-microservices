package com.esthetic.usermicroservices.repository;

import com.esthetic.usermicroservices.entity.PaymentPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentPlanRepository extends JpaRepository<PaymentPlan, Long> {
}