package com.esthetic.usermicroservices.controller;

import com.esthetic.usermicroservices.dto.ResponseDTO;
import com.esthetic.usermicroservices.dto.UserDTO;
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
        try{
            return userConfigService._GetLocationByUser(idUser);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
    @GetMapping("/get-my-current-plan/{id-user}")
    public ResponseDTO GetMyCurrentPlan(@PathVariable("id-user") String idUser) {
        try{
            return userConfigService._GetMyCurrentPlan(idUser);
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
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
    @PutMapping("/save-profile-picture")
    public ResponseDTO saveProfilePicture(@RequestBody UserDTO userDTO) {
        try{
            return userConfigService._SaveprofilePicture(userDTO);
        }catch(Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message(ex.getMessage()).build();
        }
    }
}
