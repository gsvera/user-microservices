package com.esthetic.usermicroservices.controller;

import com.esthetic.usermicroservices.dto.ResponseDTO;
import com.esthetic.usermicroservices.dto.UserDTO;
import com.esthetic.usermicroservices.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/esthetic/auth-user")
public class UserAuthController {
    @Autowired
    private UserService userService;
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO GetUserById(@PathVariable("id") Long id) {
        ResponseDTO response = new ResponseDTO();
        try{
            response.items = userService.GetUserById(id);
        } catch(Exception ex) {
            response.error = true;
            System.out.println(ex.getMessage());
        }
        return response;
    }
    @GetMapping("/get-data-user")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO GetUserByToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        ResponseDTO response = new ResponseDTO();
        try{
            response.items = userService.GetUserByToken(token);
        } catch(Exception ex) {
            response.error = true;
            System.out.print(ex.getMessage());
        }
        return response;
    }
    @RequestMapping("/update-personel-information")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO UpdatePersonalInformation(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody UserDTO userDTO) {
        ResponseDTO response = new ResponseDTO();
        try{
            userService.UpdatePersonalInformation(token, userDTO);
        } catch(Exception ex) {
            response.error = true;
            System.out.println(ex.getMessage());
        }
        return response;
    }
}