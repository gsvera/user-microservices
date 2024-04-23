package com.esthetic.usermicroservices.dto;

public class ResponseLoginDTO {
    public String token;
    public int idProfile;

    public ResponseLoginDTO(String token, int idProfile) {
        this.token = token;
        this.idProfile = idProfile;
    }
}
