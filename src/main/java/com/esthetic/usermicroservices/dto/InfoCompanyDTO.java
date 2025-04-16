package com.esthetic.usermicroservices.dto;

import com.esthetic.usermicroservices.entity.InfoCompany;

public class InfoCompanyDTO {
    public Long id;
    public String generalDescription;
    public String idUser;
    public String companyName;
    public String companyPicture;
    public String facebook;
    public String instagram;
    public String webPage;
    public String typesServices; // Parametro de auxiliar
    public InfoCompanyDTO(){} // Se requiere como constructo default
    public InfoCompanyDTO(InfoCompany infoCompany) {
        this.id = infoCompany.getId();
        this.generalDescription = infoCompany.getGeneralDescription();
        this.idUser = infoCompany.getIdUser();
        this.companyName = infoCompany.getCompanyName();
        this.companyPicture = infoCompany.getCompanyPicture();
        this.facebook = infoCompany.getFacebook();
        this.instagram = infoCompany.getInstagram();
        this.webPage = infoCompany.getWebPage();
    }
}
