package com.esthetic.usermicroservices.entity;

import com.esthetic.usermicroservices.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

// SI MODIFICA ALGUN ATRIBUTO DE ESTA ENTITY VALIDAR EN OTROS MICROS QUE EL CAMBIO SEA EL MISMO ENESPECIFICO LOS
// ATRIBUTOS QUE SE NECESITAN (services-microservices)
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_user")
public class User implements UserDetails {
    @Id
    private String id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private String email;
    @Column(name = "birth_date")
    private Date birthDate;
    private String lada;
    private String phone;
    private String password;
    @Column(name = "id_profile")
    private int idProfile;
    private String token;
    @Column(name = "profile_picture_b64")
    private String profilePictureB64;
    @Column(name = "is_client")
    private Boolean isClient;
    @Column(name = "is_provider")
    private Boolean isProvider;
    @Column(name = "active_provider")
    private Boolean activeProvider;
    @Column(name = "default_state")
    private String defaultState;
    @Column(name = "default_municipality")
    private String defaultMunicipality;
    @Column(name = "token_notification")
    private String tokenNotification;
    @Column(name = "account_verification")
    private Boolean accountVerification;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    private InfoCompany userInfoCompany;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    private UserLocation userLocation;
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "updated_at")
    private Instant updatedAt;

    public User(Optional<User> user){
        this.id = user.get().getId();
        this.firstName = user.get().getFirstName();
        this.lastName = user.get().getLastName();
        this.email = user.get().getEmail();
        this.birthDate = user.get().getBirthDate();
        this.lada = user.get().getLada();
        this.phone = user.get().getPhone();
        this.password = user.get().getPassword();
        this.idProfile = user.get().getIdProfile();
        this.token = user.get().getToken();
    }
    public User(UserDTO userDTO) {
        this.id = userDTO.getId();
    }
    public User(String idUser) {
        this.id = idUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null; // AQUI ENTRARIA LA LOGICA POR PERFILES (ROLES)
    }

    @Override
    public String getUsername() {
        return this.id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
