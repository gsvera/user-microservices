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
    public UserDTO userDTO;
    private double latitude;
    private double longitude;
    public String idUser; // aux

    public UserLocationDTO(UserLocation userLocation) {
        this.id = userLocation.getId();
        this.userDTO = new UserDTO(userLocation.getUser());
        this.latitude = userLocation.getLatitude();
        this.longitude = userLocation.getLongitude();
    }
    public UserLocationDTO(UserLocation userLocation, Boolean includeUser) {
        this.id = userLocation.getId();
        this.latitude = userLocation.getLatitude();
        this.longitude = userLocation.getLongitude();
        if(!includeUser) {
            this.userDTO = new UserDTO(userLocation.getUser());
        }
    }
}
