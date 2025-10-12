package com.esthetic.usermicroservices.dto;

import java.time.Instant;

public class PaymentPlanDTO {
    public Long id;
    public String idUser;
    public Long planId;
    public Double amountPaid;
    public Double discountApplied;
    public Long couponId;
    public Instant paymentDate;
    public String paymentMethod;
    public String codeCoupon;
    public Instant createdAt;
    public String customerStripe;
    public String paymentIntentStripe;
    public String ephemeralKeyStripe;
    public String namePlan;
}
