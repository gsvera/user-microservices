package com.esthetic.usermicroservices.dto;

import com.esthetic.usermicroservices.entity.User;

public class ResponseLoginDTO {
    public String token;
    public int idProfile;
    public String idUser;
    public String defaultState;
    public String defaultMunicipality;
    public Boolean accountVerification;

    public ResponseLoginDTO(String token, User user) {
        this.token = token;
        this.idProfile = user.getIdProfile();
        this.idUser = user.getId();
        this.defaultState = user.getDefaultState();
        this.defaultMunicipality = user.getDefaultMunicipality();
        this.accountVerification = user.getAccountVerification();
    }
}
