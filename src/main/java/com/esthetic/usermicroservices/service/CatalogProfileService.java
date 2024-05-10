package com.esthetic.usermicroservices.service;

import com.esthetic.usermicroservices.dto.CatalogProfileDTO;
import com.esthetic.usermicroservices.entity.CatalogProfile;
import com.esthetic.usermicroservices.repository.CatalogProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CatalogProfileService {
    private final CatalogProfileRepository catalogProfileRepository;

    public CatalogProfileDTO getById(long id) {
        Optional<CatalogProfile> catalogProfile = catalogProfileRepository.findById(id);
        return new CatalogProfileDTO(catalogProfile);
    }
}
