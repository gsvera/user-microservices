package com.esthetic.usermicroservices.repository;

import com.esthetic.usermicroservices.entity.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ResetTokenRepository extends JpaRepository<ResetToken, Long> {
    Optional<ResetToken> findByToken(String token);
    @Transactional @Modifying
    @Query(value = "UPDATE tbl_reset_token rt SET rt.status = 1 WHERE rt.token = ?1", nativeQuery = true)
    int updateStatusByToken(String token);

}
