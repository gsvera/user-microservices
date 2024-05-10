package com.esthetic.usermicroservices.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class LoginRequestDTO {
    public String username;
    public String password;
}
