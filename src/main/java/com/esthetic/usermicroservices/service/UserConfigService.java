package com.esthetic.usermicroservices.service;

import com.esthetic.usermicroservices.dto.ResponseDTO;
import com.esthetic.usermicroservices.dto.UserDTO;
import com.esthetic.usermicroservices.dto.UserLocationDTO;
import com.esthetic.usermicroservices.dto.UserPlanDTO;
import com.esthetic.usermicroservices.entity.User;
import com.esthetic.usermicroservices.entity.UserLocation;
import com.esthetic.usermicroservices.repository.UserLocationRepository;
import com.esthetic.usermicroservices.repository.UserPlanRepository;
import com.esthetic.usermicroservices.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserConfigService {
    private final UserLocationRepository userLocationRepository;
    private final UserRepository userRepository;
    private final UserPlanRepository userPlanRepository;

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
    public ResponseDTO _SaveprofilePicture(UserDTO userDTO) {
        Optional<User> user = userRepository.findById(userDTO.getId());
        if(user.isPresent()) {
            user.orElseThrow().setProfilePictureB64(userDTO.getProfilePictureB64());
            userRepository.save(user.get());
            return ResponseDTO.builder().message("Imagen actualizada con éxito").build();
        }
        return ResponseDTO.builder().error(true).message("No se encontro el registro").build();
    }
    public ResponseDTO _GetMyCurrentPlan(String idUser) {
        Optional<UserPlanDTO> userPlanDTO = userPlanRepository.findByIdUser(idUser);
        return ResponseDTO.builder().items(userPlanDTO.get()).build();
    }
}
