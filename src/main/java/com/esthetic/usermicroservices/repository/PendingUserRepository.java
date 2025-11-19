package com.esthetic.usermicroservices.repository;

import com.esthetic.usermicroservices.entity.PendingUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PendingUserRepository extends JpaRepository<PendingUser, Long> {
    Optional<PendingUser> findBySessionId(String sessionId);
    @Transactional
    @Modifying
    @Query(value = "UPDATE PendingUser SET statusPayment = 'completed' WHERE sessionId = ?1")
    void markCompletePayment(String sessionId);
}
