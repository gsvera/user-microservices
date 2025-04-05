package com.esthetic.usermicroservices.service;

import com.esthetic.usermicroservices.dto.CouponDTO;
import com.esthetic.usermicroservices.dto.ResponseDTO;
import com.esthetic.usermicroservices.entity.Coupon;
import com.esthetic.usermicroservices.repository.CouponRepository;
import com.esthetic.usermicroservices.utils.DateExpiredValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;

    public ResponseDTO _GetCoupon(String code) {
        Optional<Coupon> coupon = couponRepository.findByCode(code);
        if(coupon.isPresent()) {
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
