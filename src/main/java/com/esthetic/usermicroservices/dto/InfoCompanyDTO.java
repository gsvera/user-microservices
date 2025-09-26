package com.esthetic.usermicroservices.dto;

import com.esthetic.usermicroservices.entity.InfoCompany;

public class InfoCompanyDTO {
    public Long id;
    public String generalDescription;
    public UserDTO userDTO;
    public String companyName;
    public String companyPictureUrl;
    public String facebook;
    public String instagram;
    public String webPage;
    public String idUser; // parametro auxiliar
    public String typesServices; // Parametro de auxiliar
    public String auxState;
    public String auxMunicipality;
    public Double auxRating;
    public InfoCompanyDTO(){} // Se requiere como constructo default
    public InfoCompanyDTO(InfoCompany infoCompany) {
        this.id = infoCompany.getId();
        this.generalDescription = infoCompany.getGeneralDescription();
        this.userDTO = new UserDTO(infoCompany.getUser());
        this.companyName = infoCompany.getCompanyName();
        this.companyPictureUrl = infoCompany.getCompanyPictureUrl();
        this.facebook = infoCompany.getFacebook();
        this.instagram = infoCompany.getInstagram();
        this.webPage = infoCompany.getWebPage();
    }
    public InfoCompanyDTO(InfoCompany infoCompany, boolean includeUser) {
        this.id = infoCompany.getId();
        this.generalDescription = infoCompany.getGeneralDescription();
        this.companyName = infoCompany.getCompanyName();
        this.companyPictureUrl = infoCompany.getCompanyPictureUrl();
        this.facebook = infoCompany.getFacebook();
        this.instagram = infoCompany.getInstagram();
        this.webPage = infoCompany.getWebPage();
        if(!includeUser) {
            this.userDTO = new UserDTO(infoCompany.getUser());
        }
    }
}
