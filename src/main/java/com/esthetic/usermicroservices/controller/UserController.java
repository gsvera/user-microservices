package com.esthetic.usermicroservices.controller;

import com.esthetic.usermicroservices.clases.RequestTokenReset;
import com.esthetic.usermicroservices.dto.LoginRequestDTO;
import com.esthetic.usermicroservices.dto.ResponseDTO;
import com.esthetic.usermicroservices.dto.UserDTO;
import com.esthetic.usermicroservices.service.InfoCompanyService;
import com.esthetic.usermicroservices.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/esthetic/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private InfoCompanyService infoCompanyService;

    @GetMapping("/find-duplicated-user")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO findDuplicatedUser(@RequestParam String email, @RequestParam String phone) {
        ResponseDTO response = new ResponseDTO();
        try{
            return userService.findUser(email, phone);
        } catch(Exception ex) {
            response.error = true;
            response.message = "Ocurrio un error intentelo mas tarde";
        }
        return response;
    }

    @PostMapping("/save/user-sthetic-work")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO SaveUserWorker(@RequestBody UserDTO userDTO) {
        try{
            return userService._SaveUserStheticWork(userDTO);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return  ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
    @PostMapping("/save/user-sthetic-client")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO SaveUserClient(@RequestBody UserDTO userDTO) {
        try{
            return userService._SaveUserStheticClient(userDTO);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO LoginUser(@RequestBody LoginRequestDTO userLogin) {
        try{
            return userService._Login(userLogin);
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
    @PostMapping("/request-reset-password")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO RequestResetPassword(@RequestBody RequestTokenReset requestData) {
        ResponseDTO response = new ResponseDTO();
        try{
            response.error = userService.SendResetPassword(requestData).error;

        }catch (Exception ex) {
            System.out.println(ex.getMessage());
            response.error = true;
        }
        return response;
    }
    @PostMapping("/save-reset-password")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO SaveResetPassword(@RequestBody RequestTokenReset requestData) {
        ResponseDTO response = new ResponseDTO();
        try{
            response.error = userService.SaveResetPassword(requestData).error;
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return response;
    }
    @GetMapping("/get-provider-available")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO GetProviderAvailable(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String typeService,
            @RequestParam(required = false, defaultValue = "") String word,
            @RequestParam(required = false, defaultValue = "") String defaultState,
            @RequestParam(required = false, defaultValue = "") String defaultMunicipality
            ) {
        try{
            return infoCompanyService._GetProviderAvailable(page, size, typeService, word, defaultState, defaultMunicipality);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
    @GetMapping("/get-provider-by-id/{id-user}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO GetProviderById(@PathVariable(name = "id-user") String idUser) {
        try {
            return  infoCompanyService._GetProvidedrById(idUser);
        } catch (Exception ex) {
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
}
