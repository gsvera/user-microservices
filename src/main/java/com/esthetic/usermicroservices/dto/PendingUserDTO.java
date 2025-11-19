package com.esthetic.usermicroservices.dto;

import com.esthetic.usermicroservices.entity.PendingUser;

import java.time.Instant;

public class PendingUserDTO {
    public Long id;
    public String sessionId;
    public String userId;
    public String formDataJson;
    public Instant createdAt;
    public String statusPayment;
    public String origin;
    public PendingUserDTO(PendingUser pendingUser) {
        this.id = pendingUser.getId();
        this.sessionId = pendingUser.getSessionId();
        this.userId =pendingUser.getUserId();
        this.createdAt = pendingUser.getCreatedAt();
        this.statusPayment = pendingUser.getStatusPayment();
        this.origin = pendingUser.getOrigin();
    }
}
