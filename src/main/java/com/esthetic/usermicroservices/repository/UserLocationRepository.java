package com.esthetic.usermicroservices.repository;

import com.esthetic.usermicroservices.entity.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {
    Optional<UserLocation> findByIdUser(String idUser);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tbl_user_location WHERE id_user = ?1", nativeQuery = true)
    void deleteByUserId(String idUser);
}
