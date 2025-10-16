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
    //12 horas × 60 min × 60 seg × 1000 ms = 43,200,000 ms
    @Scheduled(fixedDelay = 86400000) // Se ejecuta cada dia
    @Transactional
    public void ExectuteDeleteUserInvalid() {
        try{
            userService._FindUserInactive();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    @Scheduled(fixedDelay = 86400000) // Se ejecuta cada dia
    @Transactional
    public void ExecutePrevNotificationToEndPlan(){
        try{
            userService._SendNotificationToPrevEndPlan();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    @Scheduled(fixedDelay = 43200000) // Se ejecuta cada 12 horas
    @Transactional
    public void ExecuteEnabledProvider() {
        try{
            userService._DisableProviderByEndPlan();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
