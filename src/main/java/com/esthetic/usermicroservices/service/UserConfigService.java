package com.esthetic.usermicroservices.service;

import com.esthetic.usermicroservices.dto.ResponseDTO;
import com.esthetic.usermicroservices.dto.UserLocationDTO;
import com.esthetic.usermicroservices.entity.UserLocation;
import com.esthetic.usermicroservices.repository.UserLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserConfigService {
    private final UserLocationRepository userLocationRepository;

    public ResponseDTO _GetLocationByUser(String idUser) {
        Optional<UserLocation> userLocation = userLocationRepository.findByIdUser(idUser);
        if(userLocation.isPresent()) {
            return ResponseDTO.builder().error(false).items(new UserLocationDTO(userLocation.get())).build();
        } else {
            return ResponseDTO.builder().error(true).message("No se encontro una ubicación guardada").build();
        }
    }
    public ResponseDTO _SaveUserLocation(UserLocationDTO userLocationDTO) {
        Optional<UserLocation> userLocation = userLocationRepository.findByIdUser(userLocationDTO.getIdUser());
        if(userLocation.isPresent()) {
            UserLocation newLocation = new UserLocation(userLocation.get().getId(),userLocation.get().getIdUser(), userLocationDTO.getLatitude(), userLocationDTO.getLongitude());
            userLocationRepository.save(newLocation);
        } else {
            UUID uuid = UUID.randomUUID();
            userLocationDTO.setId(uuid.toString());
            userLocationRepository.save(new UserLocation(userLocationDTO)).getId();
        }
        return ResponseDTO.builder().error(false).message("Se ha guardado la ubicación con éxito").build();

    }
}
