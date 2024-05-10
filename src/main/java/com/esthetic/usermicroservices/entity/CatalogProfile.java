package com.esthetic.usermicroservices.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Data
@Entity
@Table(name = "tbl_catalog_profile")
public class CatalogProfile {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Getter @Column(name = "profile_name_es")
    private String profileNameEs;
    @Getter @Column(name = "profile_name_en")
    private String profileNameEn;
    @Getter @Column(name = "description_es")
    private String descriptionEs;
    @Getter @Column(name = "description_en")
    private String descriptionEn;
}
