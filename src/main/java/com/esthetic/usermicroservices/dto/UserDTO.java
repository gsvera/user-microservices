package com.esthetic.usermicroservices.dto;

import com.esthetic.usermicroservices.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
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
    public String profilePicture;
    public int planSelect;
    public String tokenNotification;
    public CatalogProfileDTO catalogProfileDTO = null;
    public PaymentPlanDTO paymentPlanDTO;
    public InfoCompanyDTO infoCompanyDTO;
    public UserLocationDTO userLocationDTO;
    public String typeServices;
    public Boolean accountVerification;
    public Instant createdAt;
    public Instant updatedAt;
    public UserDTO(Optional<User> user){
        this.id = user.get().getId();
        this.firstName = user.get().getFirstName();
        this.lastName = user.get().getLastName();
        this.email = user.get().getEmail();
        this.birthDate = user.get().getBirthDate();
        this.lada = user.get().getLada();
        this.phone = user.get().getPhone();
        this.idProfile = user.get().getIdProfile();
        this.profilePicture = user.get().getProfilePicture();

        if(user.get().getUserInfoCompany() != null) {
            InfoCompanyDTO infoCompanyDTO = new InfoCompanyDTO(user.get().getUserInfoCompany());
            this.infoCompanyDTO = infoCompanyDTO;
        }

        if(user.get().getUserLocation() != null) {
            UserLocationDTO userLocationDTO = new UserLocationDTO(user.get().getUserLocation());
            this.userLocationDTO = userLocationDTO;
        }
    }
    public UserDTO(Optional<User> user, Boolean includeDetail){
        this.id = user.get().getId();
        this.firstName = user.get().getFirstName();
        this.lastName = user.get().getLastName();
        this.email = user.get().getEmail();
        this.lada = user.get().getLada();
        this.phone = user.get().getPhone();
        this.profilePicture = user.get().getProfilePicture();

        if(user.get().getUserInfoCompany() != null) {
            InfoCompanyDTO infoCompanyDTO = new InfoCompanyDTO(user.get().getUserInfoCompany(), includeDetail);
            this.infoCompanyDTO = infoCompanyDTO;
        }

        if(user.get().getUserLocation() != null) {
            UserLocationDTO userLocationDTO = new UserLocationDTO(user.get().getUserLocation(), includeDetail);
            this.userLocationDTO = userLocationDTO;
        }
    }
    public UserDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.idProfile = user.getIdProfile();
        this.profilePicture = user.getProfilePicture();
        this.lada = user.getLada();
        this.phone = user.getPhone();
        this.accountVerification = user.getAccountVerification();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
    public UserDTO(String id,String firstName, String lastName, String email, String phone, String lada) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.lada = lada;
    }
    public Boolean _GetVerification() {
        return this.accountVerification;
    }
}
