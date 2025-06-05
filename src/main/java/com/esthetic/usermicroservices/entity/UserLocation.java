package com.esthetic.usermicroservices.entity;

import com.esthetic.usermicroservices.dto.UserLocationDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

// SI MODIFICA ALGUN ATRIBUTO DE ESTA ENTITY VALIDAR EN OTROS MICROS QUE EL CAMBIO SEA EL MISMO ENESPECIFICO LOS
// ATRIBUTOS QUE SE NECESITAN (services-microservices)
@Entity
@Table(name = "tbl_user_location")
@Data
@NoArgsConstructor
public class UserLocation {
    @Id
    private String id;
    @OneToOne
    @JoinColumn(name = "id_user", insertable = true, updatable = true, nullable = false)
    @JsonBackReference
    private User user;
    private Double latitude;
    private Double longitude;
    @Column(name = "id_state")
    private Long idState;
    @Column(name = "id_municipality")
    private Long idMunicipality;
    @Column(name = "aux_state")
    private String auxState;
    @Column(name = "aux_municipality")
    private String auxMunicipality;
    private String reference;
    public UserLocation(UserLocationDTO userLocationDTO) {
        this.id = userLocationDTO.getId();
        this.user = new User(userLocationDTO.getUserDTO());
        this.latitude = userLocationDTO.getLatitude();
        this.longitude = userLocationDTO.getLongitude();
        this.idState = userLocationDTO.idState;
        this.idMunicipality = userLocationDTO.idMunicipality;
        this.auxState = userLocationDTO.auxState;
        this.auxMunicipality = userLocationDTO.auxMunicipality;
        this.reference = userLocationDTO.reference;
    }
    public UserLocation(String id, String idUser, double latitude, double longitude) {
        this.id = id;
        this.user = new User(idUser);
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
