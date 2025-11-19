package com.esthetic.usermicroservices.service;

import com.esthetic.usermicroservices.dto.CheckoutStripeParamsDTO;
import com.esthetic.usermicroservices.entity.PendingUser;
import com.esthetic.usermicroservices.repository.PendingUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PendingUserService {
    private final PendingUserRepository pendingUserRepository;
    public PendingUser _CreatePendingUser(String sessionId, CheckoutStripeParamsDTO checkoutStripeParamsDTO) {
        PendingUser pending = new PendingUser(sessionId, checkoutStripeParamsDTO);
        return pendingUserRepository.save(pending);
    }
    public Optional<PendingUser> _GetBySessionIdObject(String sessionId) {
        return pendingUserRepository.findBySessionId(sessionId);

    }

    public void _MarkCompleted(String sessionId) {
        pendingUserRepository.markCompletePayment(sessionId);
    }
}
