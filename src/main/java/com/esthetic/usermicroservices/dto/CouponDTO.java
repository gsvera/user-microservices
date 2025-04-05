package com.esthetic.usermicroservices.dto;

import com.esthetic.usermicroservices.entity.Coupon;

import java.time.LocalDateTime;

public class CouponDTO {
    public Long id;
    public String code;
    public Double discountAmount;
    public Double discountPercentage;
    public int maxUsage;
    public int countUsage;
    public LocalDateTime validFrom;
    public LocalDateTime validUntil;
    public Boolean isActive;
    public CouponDTO(Coupon coupon) {
        this.id = coupon.getId();
        this.code = coupon.getCode();
        this.discountAmount = coupon.getDiscountAmount();
        this.discountPercentage = coupon.getDiscountPercentage();
        this.maxUsage = coupon.getMaxUsage();
        this.countUsage = coupon.getCountUsage();
        this.validFrom = coupon.getValidFrom();
        this.validUntil = coupon.getValidUntil();
        this.isActive = coupon.getIsActive();
    }
}
