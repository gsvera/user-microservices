package com.esthetic.usermicroservices.repository;

import com.esthetic.usermicroservices.dto.UserPlanDTO;
import com.esthetic.usermicroservices.entity.UserPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserPlanRepository extends JpaRepository<UserPlan, Long> {
    @Query(value = "SELECT new com.esthetic.usermicroservices.dto.UserPlanDTO(u, c) FROM UserPlan AS u LEFT JOIN u.catalogPlan c WHERE isUsed = false AND u.idUser = ?1 ORDER BY u.startDate ASC")
    Optional<UserPlanDTO> findByIdUser(String idUser);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tbl_user_plan WHERE id_user = ?1", nativeQuery = true)
    void deleteAllPlanByUser(String idUser);
    @Query(value = "SELECT p FROM UserPlan p WHERE isActive = true AND endDate < ?1")
    List<UserPlan> listPlanExpired(Instant today);

}
