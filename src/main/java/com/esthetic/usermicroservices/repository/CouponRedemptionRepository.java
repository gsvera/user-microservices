package com.esthetic.usermicroservices.repository;

import com.esthetic.usermicroservices.entity.CouponRedemption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRedemptionRepository extends JpaRepository<CouponRedemption, Long> {
}
