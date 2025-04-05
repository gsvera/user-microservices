package com.esthetic.usermicroservices.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tbl_coupon")
public class Coupon {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    @Column(name = "discount_amount")
    private Double discountAmount;
    @Column(name = "discount_percentage")
    private Double discountPercentage;
    @Column(name = "max_usage")
    private int maxUsage;
    @Column(name = "count_usage")
    private int countUsage;
    @Column(name = "valid_from")
    private LocalDateTime validFrom;
    @Column(name="valid_until")
    private LocalDateTime validUntil;
    @Column(name = "is_active")
    private Boolean isActive;
}
