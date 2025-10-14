package com.esthetic.usermicroservices.repository;

import com.esthetic.usermicroservices.entity.FavoriteProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FavoriteProviderRepository extends JpaRepository<FavoriteProvider, Long> {
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM FavoriteProvider f WHERE f.idClient = ?1 AND f.idProvider = ?2")
    void deleteFavoriteProviderByClient(String idClient, String idProvider);

    @Query(value = "SELECT f.idProvider FROM FavoriteProvider f WHERE f.idClient = ?1")
    List<String> findKeysFavoriteProvider(String idClient);
}
