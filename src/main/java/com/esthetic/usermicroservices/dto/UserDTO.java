package com.esthetic.usermicroservices.dto;

import com.esthetic.usermicroservices.entity.User;
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

    private String token;
    private CatalogProfileDTO catalogProfileDTO = null;

    public UserDTO(Optional<User> user){
        this.id = user.get().getId();
        this.firstName = user.get().getFirstName();
        this.lastName = user.get().getLastName();
        this.email = user.get().getEmail();
        this.birthDate = user.get().getBirthDate();
        this.phone = user.get().getPhone();
        this.idProfile = user.get().getIdProfile();
    }
}
