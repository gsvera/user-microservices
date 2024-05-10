package com.esthetic.usermicroservices.dto;

import com.esthetic.usermicroservices.entity.CatalogProfile;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
public class CatalogProfileDTO {
    @Id
    @Getter
    private Long id;
    @Getter
    private String profileNameEs;
    @Getter
    private String profileNameEn;
    @Getter
    private String descriptionEs;
    @Getter
    private String descriptionEn;

    public CatalogProfileDTO(Optional<CatalogProfile> catalogProfile) {
        this.id = catalogProfile.get().getId();
        this.profileNameEs = catalogProfile.get().getProfileNameEs();
        this.profileNameEn = catalogProfile.get().getProfileNameEn();
        this.descriptionEs = catalogProfile.get().getDescriptionEs();
        this.descriptionEn = catalogProfile.get().getDescriptionEn();
    }
}