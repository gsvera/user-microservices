package com.esthetic.usermicroservices.service;

import com.esthetic.usermicroservices.dto.PaymentPlanDTO;
import com.esthetic.usermicroservices.dto.ResponseDTO;
import com.esthetic.usermicroservices.entity.Coupon;
import com.esthetic.usermicroservices.entity.CouponRedemption;
import com.esthetic.usermicroservices.entity.PaymentPlan;
import com.esthetic.usermicroservices.repository.CouponRedemptionRepository;
import com.esthetic.usermicroservices.repository.CouponRepository;
import com.esthetic.usermicroservices.repository.PaymentPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentPlanRepository paymentPlanRepository;
    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository couponRedemptionRepository;
    public ResponseDTO _CreatePaymentPlan(PaymentPlanDTO paymentPlanDTO) {
        Optional<Coupon> coupon = null;
        if(!paymentPlanDTO.codeCoupon.isEmpty()){
             coupon = couponRepository.findByCode(paymentPlanDTO.codeCoupon);
            if(coupon.isPresent()) {
                paymentPlanDTO.couponId = coupon.get().getId();
                paymentPlanDTO.discountApplied = coupon.get().getDiscountAmount();
            }
        }
        PaymentPlan paymentPlan = new PaymentPlan(paymentPlanDTO);
        paymentPlanRepository.save(paymentPlan);

        if(coupon.isPresent()) {
            couponRedemptionRepository.save(new CouponRedemption(paymentPlanDTO.idUser, coupon.get().getId(), paymentPlan.getId()));
            coupon.orElseThrow().setCountUsage(coupon.get().getCountUsage() + 1);
            couponRepository.save(coupon.get());
        }

        return ResponseDTO.builder().message("Pago creado con éxito").build();
    }
}
