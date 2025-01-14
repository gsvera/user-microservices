package com.esthetic.usermicroservices.entity;

import com.esthetic.usermicroservices.dto.UserDTO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

@Entity
@Data
@ToString
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null; // AQUI ENTRARIA LA LOGICA POR PERFILES (ROLES)
    }

    @Override
    public String getUsername() {
        return this.email;
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
