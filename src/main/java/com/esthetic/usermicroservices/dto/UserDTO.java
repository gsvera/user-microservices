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
    public String id;
    public String firstName;
    public String lastName;
    public String email;
    public Date birthDate;
    public String lada;
    public String phone;
    public String password;
    public int idProfile;
    public String token;
    public String profilePictureB64;
    public int planSelect;
    public CatalogProfileDTO catalogProfileDTO = null;
    public PaymentPlanDTO paymentPlanDTO;
    public String typeServices;
    public UserDTO(Optional<User> user){
        this.id = user.get().getId();
        this.firstName = user.get().getFirstName();
        this.lastName = user.get().getLastName();
        this.email = user.get().getEmail();
        this.birthDate = user.get().getBirthDate();
        this.lada = user.get().getLada();
        this.phone = user.get().getPhone();
        this.idProfile = user.get().getIdProfile();
        this.profilePictureB64 = user.get().getProfilePictureB64();
    }
    public UserDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.idProfile = user.getIdProfile();
        this.profilePictureB64 = user.getProfilePictureB64();
        this.lada = user.getLada();
        this.phone = user.getPhone();
    }
}
