package com.esthetic.usermicroservices.service;

import com.esthetic.usermicroservices.dto.CouponDTO;
import com.esthetic.usermicroservices.dto.ResponseDTO;
import com.esthetic.usermicroservices.entity.Coupon;
import com.esthetic.usermicroservices.entity.CouponRedemption;
import com.esthetic.usermicroservices.repository.CouponRedemptionRepository;
import com.esthetic.usermicroservices.repository.CouponRepository;
import com.esthetic.usermicroservices.utils.DateExpiredValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository couponRedemptionRepository;

    public ResponseDTO _GetCoupon(String code, String idUser) {
        Optional<Coupon> coupon = couponRepository.findByCode(code);
        if(coupon.isPresent()) {
            if(idUser != null) {
                List<CouponRedemption> couponRedemption = couponRedemptionRepository.findCouponUsage(idUser, coupon.get().getId());
                if(couponRedemption.size() > 0) {
                    return ResponseDTO.builder().error(true).message("Ya has utilizado el coupon").build();
                }
            }
            LocalDateTime now = LocalDateTime.now();
            if(DateExpiredValidator.isNotYetValid(coupon.get().getValidFrom(), now)) {
                return ResponseDTO.builder().error(true).message("El cupón aún no es válido").build();
            }
            if(DateExpiredValidator.isExpired(coupon.get().getValidUntil(), now)){
                return ResponseDTO.builder().error(true).message("El cupón  ha caducado").build();
            }
            return ResponseDTO.builder().items(new CouponDTO(coupon.get())).build();
        } else {
            return ResponseDTO.builder().error(true).message("No se encontro el cupon").build();
        }
    }
}
