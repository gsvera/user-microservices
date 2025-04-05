package com.esthetic.usermicroservices.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity @Data @Table(name = "tbl_coupon_redemption")
public class CouponRedemption {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "id_user")
    private String idUser;
    @Column(name = "coupon_id")
    private Long couponId;
    @Column(name = "payment_id")
    private Long paymentId;
    @Column(name = "redeemde_at")
    private Timestamp redeemdeAt;
    public CouponRedemption(String idUser, Long couponId, Long paymentId) {
        this.idUser = idUser;
        this.couponId = couponId;
        this.paymentId = paymentId;
    }
}
