package com.esthetic.usermicroservices.repository;

import com.esthetic.usermicroservices.entity.ConfigAppMobile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ConfigAppMobileRepository extends JpaRepository<ConfigAppMobile, Long> {
    @Query(value = "SELECT * FROM tbl_config_app_mobile ORDER BY id ASC LIMIT 1", nativeQuery = true)
    Optional<ConfigAppMobile> getConfigApp();
}
