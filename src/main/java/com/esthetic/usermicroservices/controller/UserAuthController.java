package com.esthetic.usermicroservices.controller;

import com.esthetic.usermicroservices.dto.ResponseDTO;
import com.esthetic.usermicroservices.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
        System.out.println("id: "+id);
        ResponseDTO response = new ResponseDTO();
        try{
            response.items = userService.GetUserById(id);
        } catch(Exception ex) {
            response.error = true;
            response.message = ex.getMessage();
        }
        return response;
    }
}