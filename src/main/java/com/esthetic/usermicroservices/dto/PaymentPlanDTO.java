package com.esthetic.usermicroservices.dto;

import java.time.LocalDateTime;

public class PaymentPlanDTO {
    public Long id;
    public String idUser;
    public Long planId;
    public Double amountPaid;
    public Double discountApplied;
    public Long couponId;
    public LocalDateTime paymentDate;
    public String paymentMethod;
    public String transactionId;
    public String codeCoupon;
}
