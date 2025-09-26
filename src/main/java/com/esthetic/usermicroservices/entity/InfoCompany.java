package com.esthetic.usermicroservices.entity;

import com.esthetic.usermicroservices.dto.InfoCompanyDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tbl_info_company")
public class InfoCompany {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "general_description")
    private String generalDescription;
    @OneToOne
    @JoinColumn(name = "id_user", insertable = true, updatable = true, nullable = false)
    @JsonBackReference
    private User user;
    @Column(name = "company_name")
    private String companyName;
    @Column(name = "company_picture_url")
    private String companyPictureUrl;
    private String facebook;
    private String instagram;
    @Column(name = "web_page")
    private String webPage;
    public InfoCompany(){} // Jpa lo requiere como constructor default
    public InfoCompany(InfoCompanyDTO infoCompanyDTO) {
        this.generalDescription = infoCompanyDTO.generalDescription;
        this.user = new User(infoCompanyDTO.userDTO);
        this.companyName = infoCompanyDTO.companyName;
        this.companyPictureUrl = infoCompanyDTO.companyPictureUrl;
        this.facebook = infoCompanyDTO.facebook;
        this.instagram = infoCompanyDTO.instagram;
        this.webPage = infoCompanyDTO.webPage;
    }
}
