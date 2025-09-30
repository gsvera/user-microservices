package com.esthetic.usermicroservices.repository;

import com.esthetic.usermicroservices.entity.CouponRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CouponRedemptionRepository extends JpaRepository<CouponRedemption, Long> {
    @Query(value = "SELECT c FROM CouponRedemption c WHERE c.idUser = ?1 AND c.couponId = ?2")
    List<CouponRedemption> findCouponUsage(String idUser, Long couponId);
}
