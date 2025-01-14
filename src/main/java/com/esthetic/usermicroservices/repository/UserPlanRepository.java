package com.esthetic.usermicroservices.repository;

import com.esthetic.usermicroservices.entity.UserPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPlanRepository extends JpaRepository<UserPlan, Long> {
}
