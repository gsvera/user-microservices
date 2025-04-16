package com.esthetic.usermicroservices.entity;

import com.esthetic.usermicroservices.dto.InfoCompanyDTO;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "tbl_info_company")
public class InfoCompany {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "general_description")
    private String generalDescription;
    @Column(name = "id_user")
    private String idUser;
    @Column(name = "company_name")
    private String companyName;
    @Column(name = "company_picture")
    private String companyPicture;
    private String facebook;
    private String instagram;
    @Column(name = "web_page")
    private String webPage;
    public InfoCompany(){} // Jpa lo requiere como constructor default
    public InfoCompany(InfoCompanyDTO infoCompanyDTO) {
        this.generalDescription = infoCompanyDTO.generalDescription;
        this.idUser = infoCompanyDTO.idUser;
        this.companyName = infoCompanyDTO.companyName;
        this.companyPicture = infoCompanyDTO.companyPicture;
        this.facebook = infoCompanyDTO.facebook;
        this.instagram = infoCompanyDTO.instagram;
        this.webPage = infoCompanyDTO.webPage;
    }
}
