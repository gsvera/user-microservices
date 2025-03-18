package com.esthetic.usermicroservices.controller;

import com.esthetic.usermicroservices.dto.ResponseDTO;
import com.esthetic.usermicroservices.dto.UserLocationDTO;
import com.esthetic.usermicroservices.service.UserConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/esthetic/auth-user-config")
public class UserConfigController
{
    @Autowired
    private UserConfigService userConfigService;
    @GetMapping("/get-location-by-user")
    public ResponseDTO getLocationByUser(@RequestParam(name = "id-user") String idUser) {
        ResponseDTO response = new ResponseDTO();
        try{
            return userConfigService._GetLocationByUser(idUser);
        } catch (Exception ex) {
            response.error = true;
            response.message = "Ocurrio un error intentelo mas tarde";
            System.out.println(ex.getMessage());
        }
        return response;
    }
    @PostMapping("/save-location")
    public ResponseDTO saveLocation(@RequestBody UserLocationDTO userLocationDTO) {
        ResponseDTO response = new ResponseDTO();
        try{
            return userConfigService._SaveUserLocation(userLocationDTO);
        } catch(Exception ex) {
            response.error = true;
            response.message = "Ocurrio un error intentelo mas tarde";
            System.out.println(ex.getMessage());
        }
        return response;
    }
}
