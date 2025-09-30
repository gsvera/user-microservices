package com.esthetic.usermicroservices.controller;

import com.esthetic.usermicroservices.dto.ResponseDTO;
import com.esthetic.usermicroservices.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/esthetic/coupon")
public class CouponController {
    @Autowired
    private CouponService couponService;
    @GetMapping("/get-coupon")
    public ResponseDTO GetCoupon(@RequestParam(name = "code") String code, @RequestParam(name = "id-user", required = false) String idUser) {
        try{
            return couponService._GetCoupon(code, idUser);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
}