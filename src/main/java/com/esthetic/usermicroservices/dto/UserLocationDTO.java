package com.esthetic.usermicroservices.dto;

import com.esthetic.usermicroservices.entity.UserLocation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLocationDTO {
    private String id;
    private String idUser;
    private double latitude;
    private double longitude;

    public UserLocationDTO(UserLocation userLocation) {
        this.id = userLocation.getId();
        this.idUser = userLocation.getIdUser();
        this.latitude = userLocation.getLatitude();
        this.longitude = userLocation.getLongitude();
    }
}
