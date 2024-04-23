package com.esthetic.usermicroservices.dto;

import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Date birthDate;
    private String phone;
    private String password;
    private int idProfile;
    private String nameProfile = null;

    public UserDTO(String id, String firstName, String lastName, String email, Date birthDate, String phone, int idProfile) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDate = birthDate;
        this.phone = phone;
        this.idProfile = idProfile;
    }
}
