package com.esthetic.usermicroservices.service;

import com.esthetic.usermicroservices.config.EnvConfig;
import com.esthetic.usermicroservices.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PushNotificationService {
    private final UserRepository userRepository;
    @Autowired
    private final EnvConfig envConfig;
    //
//    public ResponseDTO prueba(String id) {
//        Optional<User> user = userRepository.findById(id);
//        if(user.isPresent()) {
//            this.sendPushNotification(user.get().getTokenNotification(), "Prueba", "body");
//            return ResponseDTO.builder().message(user.get().getTokenNotification()).build();
//        }
//        return ResponseDTO.builder().error(true).message("no hay usuario").build();
//    }
    public void sendPushNotification(String tokenNotification, String title, String body) {
        String expoApiUrl = envConfig.getExpoNotificationUrl();

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> payload = new HashMap<>();
        payload.put("to", tokenNotification); // Token tipo "ExponentPushToken[xxxxxxx]"
        payload.put("title", title);
        payload.put("body", body);
        payload.put("sound", "default");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(expoApiUrl, request, String.class);
            System.out.println("Expo response: " + response.getBody());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
