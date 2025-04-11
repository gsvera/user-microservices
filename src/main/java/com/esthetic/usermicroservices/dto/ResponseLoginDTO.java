package com.esthetic.usermicroservices.dto;

public class ResponseLoginDTO {
    public String token;
    public int idProfile;
    public String idUser;

    public ResponseLoginDTO(String token, int idProfile, String idUser) {
        this.token = token;
        this.idProfile = idProfile;
        this.idUser = idUser;
    }
}
