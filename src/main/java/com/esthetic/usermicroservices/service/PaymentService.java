package com.esthetic.usermicroservices.service;

import com.esthetic.usermicroservices.dto.PaymentPlanDTO;
import com.esthetic.usermicroservices.dto.ResponseDTO;
import com.esthetic.usermicroservices.entity.Coupon;
import com.esthetic.usermicroservices.entity.CouponRedemption;
import com.esthetic.usermicroservices.entity.PaymentPlan;
import com.esthetic.usermicroservices.entity.UserPlan;
import com.esthetic.usermicroservices.repository.CouponRedemptionRepository;
import com.esthetic.usermicroservices.repository.CouponRepository;
import com.esthetic.usermicroservices.repository.PaymentPlanRepository;
import com.esthetic.usermicroservices.repository.UserPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentPlanRepository paymentPlanRepository;
    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository couponRedemptionRepository;
    private final UserPlanRepository userPlanRepository;
    public ResponseDTO _CreatePaymentPlan(PaymentPlanDTO paymentPlanDTO) {
        Optional<Coupon> coupon = null;

        if( paymentPlanDTO.codeCoupon != null && !paymentPlanDTO.codeCoupon.isEmpty()){
             coupon = couponRepository.findByCode(paymentPlanDTO.codeCoupon);
            if(coupon.isPresent()) {
                paymentPlanDTO.couponId = coupon.get().getId();
                paymentPlanDTO.discountApplied = coupon.get().getDiscountAmount();
            }
        }
        PaymentPlan paymentPlan = new PaymentPlan(paymentPlanDTO);
        paymentPlanRepository.save(paymentPlan);

        if(coupon != null && coupon.isPresent()) {
            couponRedemptionRepository.save(new CouponRedemption(paymentPlanDTO.idUser, coupon.get().getId(), paymentPlan.getId()));
            coupon.orElseThrow().setCountUsage(coupon.get().getCountUsage() + 1);
            couponRepository.save(coupon.get());
        }

        return ResponseDTO.builder().message("Pago creado con éxito").build();
    }
    public ResponseDTO _GetProviderIsActive(String idProvider){
        Optional<UserPlan> userPlan = userPlanRepository.findIsActive(idProvider);

        if(userPlan.isPresent()) {
            return ResponseDTO.builder().message("Proveedor activo").build();
        } else {
            return ResponseDTO.builder().error(true).message("Tu suscripción ha vencido. Realiza tu pago para seguir disfrutando de todas las funcionalidades de la app.").build();
        }
    }
    public ResponseDTO _GetHistoryPay(String idProvider){
        List<Object[]> listPaymentPlan =  paymentPlanRepository.findLastPayByIdUser(idProvider);
        List<PaymentPlanDTO> listPaymentPlanDTO = new ArrayList<>();
        for(Object[] item : listPaymentPlan) {
            PaymentPlanDTO paymentPlanDTO = new PaymentPlanDTO();
            paymentPlanDTO.id = (Long) item[0];
            paymentPlanDTO.amountPaid = (Double) item[1];
            paymentPlanDTO.discountApplied = (Double) item[2];
            paymentPlanDTO.paymentDate = (Instant) item[3];
            paymentPlanDTO.namePlan = (String)item[4];
            listPaymentPlanDTO.add(paymentPlanDTO);
        }
        return ResponseDTO.builder().items(listPaymentPlanDTO).build();
    }
}
