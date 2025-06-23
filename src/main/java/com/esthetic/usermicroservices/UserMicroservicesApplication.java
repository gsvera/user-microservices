package com.esthetic.usermicroservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UserMicroservicesApplication {
	public static void main(String[] args) {
		SpringApplication.run(UserMicroservicesApplication.class, args);
	}
}