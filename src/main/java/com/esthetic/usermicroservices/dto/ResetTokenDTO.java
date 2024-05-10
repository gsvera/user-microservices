package com.esthetic.usermicroservices.dto;

import com.esthetic.usermicroservices.entity.ResetToken;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResetTokenDTO {
    private Long id;
    private int status;
    private String token;
    private String email;
    private Timestamp createDate;

    public ResetTokenDTO(Optional<ResetToken> resetToken) {
        this.id = resetToken.get().getId();
        this.status = resetToken.get().getStatus();
        this.token = resetToken.get().getToken();
        this.email = resetToken.get().getEmail();
        this.createDate = resetToken.get().getCreateDate();
    }
}
