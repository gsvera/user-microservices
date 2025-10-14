package com.esthetic.usermicroservices.entity;

import com.esthetic.usermicroservices.dto.FavoriteProviderDTO;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_favorite_provider")
public class FavoriteProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "id_client")
    private String idClient;
    @Column(name = "id_provider")
    private String idProvider;
    public FavoriteProvider(FavoriteProviderDTO favoriteProviderDTO) {
        this.idClient = favoriteProviderDTO.idClient;
        this.idProvider = favoriteProviderDTO.idProvider;
    }
}
