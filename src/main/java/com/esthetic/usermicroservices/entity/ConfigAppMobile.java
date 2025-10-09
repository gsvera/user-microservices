package com.esthetic.usermicroservices.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "tbl_config_app_mobile")
public class ConfigAppMobile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "version_android")
    private String versionAndroid;
    @Column(name = "version_ios")
    private String versionIos;
}
