package com.esthetic.usermicroservices.controller;

import com.esthetic.usermicroservices.dto.LoginRequestDTO;
import com.esthetic.usermicroservices.dto.ResponseDTO;
import com.esthetic.usermicroservices.dto.UserDTO;
import com.esthetic.usermicroservices.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/esthetic/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO SaveUser(@RequestBody UserDTO userDTO) {
        ResponseDTO response = new ResponseDTO();
        try{
            return userService.SaveUser(userDTO);
        } catch (Exception ex) {
            response.error = true;
            response.message = ex.getMessage();
        }
        return response;
    }
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO LoginUser(@RequestBody LoginRequestDTO userLogin) {
        ResponseDTO response = new ResponseDTO();
        try{
            return userService.Login(userLogin);
        } catch(Exception ex) {
            response.error = true;
            response.message = ex.getMessage();
        }
        return response;
    }
}
