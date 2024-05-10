package com.esthetic.usermicroservices.service;

import com.esthetic.usermicroservices.dto.ResetTokenDTO;
import com.esthetic.usermicroservices.entity.ResetToken;
import com.esthetic.usermicroservices.repository.ResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResetTokenService {
    private final ResetTokenRepository resetTokenRepository;
    public String GenerateToken(String email) {
        UUID uuid = UUID.randomUUID();

        ResetToken newResetToken = new ResetToken();
        newResetToken.setToken(uuid.toString());
        newResetToken.setEmail(email);
        resetTokenRepository.save(newResetToken);

        return uuid.toString();
    }
    public ResetTokenDTO GetRecordByToken(String token) {
        Optional<ResetToken> resetToken = resetTokenRepository.findByToken(token);

        return new ResetTokenDTO(resetToken);
    }
    public void UpdateStatus(String token) {
        resetTokenRepository.updateStatusByToken(token);
    }
}
