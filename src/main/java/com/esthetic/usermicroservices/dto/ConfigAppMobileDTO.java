package com.esthetic.usermicroservices.dto;

import com.esthetic.usermicroservices.entity.ConfigAppMobile;

public class ConfigAppMobileDTO {
    public Long id;
    public String versionAndroid;
    public String versionIos;
    public ConfigAppMobileDTO(ConfigAppMobile configAppMobile) {
        this.id = configAppMobile.getId();
        this.versionAndroid = configAppMobile.getVersionAndroid();
        this.versionIos = configAppMobile.getVersionIos();
    }
}
