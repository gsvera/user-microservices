package com.esthetic.usermicroservices.repository;

import com.esthetic.usermicroservices.entity.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {
    Optional<UserLocation> findByIdUser(String idUser);
}
