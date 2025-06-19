package com.esthetic.usermicroservices.service;

import com.esthetic.usermicroservices.entity.ResetToken;
import com.esthetic.usermicroservices.repository.ResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResetTokenService {
    private final ResetTokenRepository resetTokenRepository;
    public String GenerateToken(String email) {
        SecureRandom secureRandom = new SecureRandom();
        Integer codigo = secureRandom.nextInt(1_000_000);
        ResetToken newResetToken = new ResetToken(codigo.toString(), email);
        resetTokenRepository.save(newResetToken);

        return codigo.toString();
    }

    public Optional<ResetToken> GetRecordByToken(String token) {
        Optional<ResetToken> resetToken = resetTokenRepository.findByToken(token);

        return resetToken;
    }

    public void _DeleteToken(String token) {
        Optional<ResetToken> resetToken = resetTokenRepository.findByToken(token);
        if(resetToken.isPresent()) {
            resetTokenRepository.delete(resetToken.get());
        }
    }
}
