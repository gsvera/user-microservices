package com.esthetic.usermicroservices.controller;

import com.esthetic.usermicroservices.dto.InfoCompanyDTO;
import com.esthetic.usermicroservices.dto.ResponseDTO;
import com.esthetic.usermicroservices.dto.UserDTO;
import com.esthetic.usermicroservices.dto.UserLocationDTO;
import com.esthetic.usermicroservices.service.UserConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/esthetic/auth-user-config")
public class UserConfigController
{
    @Autowired
    private UserConfigService userConfigService;
    @GetMapping("/get-location-by-provider")
    public ResponseDTO getLocationByUser(@RequestParam(name = "id-user") String idUser) {
        try{
            return userConfigService._GetLocationByUser(idUser);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
    @GetMapping("/get-default-location-by-client/{id-user}")
    public ResponseDTO GetDefaultLocationByClient(@PathVariable(name = "id-user") String idUser) {
        try{
            return userConfigService._GetDefaultLocationByUser(idUser);
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
        try{
            return userConfigService._SaveUserLocation(userLocationDTO);
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
    @PutMapping("/save-default-location-client/{id-user}")
    public ResponseDTO SaveDefaultLocationClient(
            @PathVariable(name = "id-user") String idUser,
            @RequestParam(name = "default-state") String defaultState,
            @RequestParam(name = "default-municipality", required = false) String defaultMunicipality
    ) {
        try {
            return userConfigService._SaveDefaultLocationClient(idUser,defaultState, defaultMunicipality);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
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
    @GetMapping("/get-info-company-by-user/{id-user}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO GetInfoCompanyByUser(@PathVariable("id-user") String idUser) {
        try{
            return userConfigService._GetInforCompanyByUser(idUser);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
    @PutMapping("/update-info-company")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO SaveInfoCompany(@RequestBody InfoCompanyDTO infoCompanyDTO) {
        try{
            return userConfigService._UpdateInfoCompany(infoCompanyDTO);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
    @PutMapping("/save-notifications-token")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO SaveNotificationToken(@RequestBody UserDTO userDTO) {
        try{
            return userConfigService._SaveNotificationToken(userDTO.id, userDTO.tokenNotification);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
}
