package com.esthetic.usermicroservices.dto;

import com.esthetic.usermicroservices.entity.FavoriteProvider;

public class FavoriteProviderDTO {
    public Long id;
    public String idClient;
    public String idProvider;
    public FavoriteProviderDTO(){}
    public FavoriteProviderDTO(FavoriteProvider favoriteProvider) {
        this.id = favoriteProvider.getId();
        this.idClient = favoriteProvider.getIdClient();
        this.idProvider = favoriteProvider.getIdProvider();
    }
}
