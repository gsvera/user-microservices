package com.esthetic.usermicroservices.repository;

import com.esthetic.usermicroservices.dto.UserPlanDTO;
import com.esthetic.usermicroservices.entity.UserPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserPlanRepository extends JpaRepository<UserPlan, Long> {
    @Query(value = "SELECT new com.esthetic.usermicroservices.dto.UserPlanDTO(u, c) FROM UserPlan AS u LEFT JOIN u.catalogPlan c WHERE isActive = true AND u.idUser = ?1 ORDER BY u.createdDate")
    Optional<UserPlanDTO> findByIdUser(String idUser);
}
