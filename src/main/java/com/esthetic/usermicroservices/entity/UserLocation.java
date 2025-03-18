package com.esthetic.usermicroservices.entity;

import com.esthetic.usermicroservices.dto.UserLocationDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_user_location")
@Data
@NoArgsConstructor
public class UserLocation {
    @Id
    private String id;
    @Column(name = "id_user")
    private String idUser;
    private double latitude;
    private double longitude;
    public UserLocation(UserLocationDTO userLocationDTO) {
        this.id = userLocationDTO.getId();
        this.idUser = userLocationDTO.getIdUser();
        this.latitude = userLocationDTO.getLatitude();
        this.longitude = userLocationDTO.getLongitude();
    }
    public UserLocation(String id, String idUser, double latitude, double longitude) {
        this.id = id;
        this.idUser = idUser;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
