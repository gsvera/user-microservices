package com.esthetic.usermicroservices.entity;

import com.esthetic.usermicroservices.dto.CheckoutStripeParamsDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(name = "tbl_pending_user")
public class PendingUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "session_id")
    private String sessionId;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "form_data_json")
    private String formDataJson;
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "status_payment")
    private String statusPayment; // "pending", "completed"
    private String origin;
    public PendingUser() {}
    public PendingUser(String sessionId, CheckoutStripeParamsDTO checkoutStripeParamsDTO) {
        this.sessionId = sessionId;
        this.formDataJson = checkoutStripeParamsDTO.jsonData;
        this.origin = checkoutStripeParamsDTO.origin;
        this.userId = checkoutStripeParamsDTO.userId;
        this.createdAt = Instant.now();
        this.statusPayment = "pending";
    }
}
