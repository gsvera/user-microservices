package com.esthetic.usermicroservices.entity;

import com.esthetic.usermicroservices.dto.PaymentPlanDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(name = "tbl_payment_plan")
public class PaymentPlan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "id_user")
    private String idUser;
    @Column(name = "plan_id")
    private Long planId;
    @Column(name = "amount_paid")
    private Double amountPaid;
    @Column(name = "discount_applied")
    private Double discountApplied;
    @Column(name = "coupon_id")
    private Long couponId;
    @Column(name = "payment_date")
    private Instant paymentDate;
    @Column(name = "payment_method")
    private String paymentMethod;
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "customer_stripe")
    private String customerStripe;
    @Column(name = "payment_intent_stripe")
    private String paymentIntentStripe;
    @Column(name = "ephemeral_key_stripe")
    private String ephemeralKeyStripe;

    public PaymentPlan(PaymentPlanDTO paymentPlanDTO){
        this.id = paymentPlanDTO.id;
        this.idUser = paymentPlanDTO.idUser;
        this.planId = paymentPlanDTO.planId;
        this.amountPaid = paymentPlanDTO.amountPaid;
        this.discountApplied = paymentPlanDTO.discountApplied;
        this.couponId = paymentPlanDTO.couponId;
        this.paymentDate = paymentPlanDTO.paymentDate;
        this.paymentMethod = paymentPlanDTO.paymentMethod;
        this.createdAt = paymentPlanDTO.paymentDate;
        this.customerStripe = paymentPlanDTO.customerStripe;
        this.paymentIntentStripe = paymentPlanDTO.paymentIntentStripe;
        this.ephemeralKeyStripe = paymentPlanDTO.ephemeralKeyStripe;
    }
}
