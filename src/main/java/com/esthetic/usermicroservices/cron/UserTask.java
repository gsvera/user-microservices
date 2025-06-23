package com.esthetic.usermicroservices.cron;

import com.esthetic.usermicroservices.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserTask {
    @Autowired
    private UserService userService;

    @Scheduled(fixedDelay = 3600000) // Se ejecuta cada hora
    @Transactional
    public void ExectuteDeleteUserInvalid() {
        try{
            userService._FindUserInactive();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
