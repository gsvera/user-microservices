package com.esthetic.usermicroservices.repository;

import com.esthetic.usermicroservices.entity.CatalogProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogProfileRepository extends JpaRepository<CatalogProfile, Long> {
}
