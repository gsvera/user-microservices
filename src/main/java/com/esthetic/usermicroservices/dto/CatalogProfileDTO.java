package com.esthetic.usermicroservices.dto;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class CatalogProfileDTO {
    @Id
    @Getter
    private Long id;
    @Getter
    private String profileName;
    @Getter
    private String description;
}