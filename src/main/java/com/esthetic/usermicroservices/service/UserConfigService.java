package com.esthetic.usermicroservices.service;

import com.esthetic.usermicroservices.dto.*;
import com.esthetic.usermicroservices.entity.InfoCompany;
import com.esthetic.usermicroservices.entity.User;
import com.esthetic.usermicroservices.entity.UserLocation;
import com.esthetic.usermicroservices.repository.InfoCompanyRepository;
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
    private final InfoCompanyRepository infoCompanyRepository;
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
    public ResponseDTO _DeleteLocationByUser(String idUser) {
        userLocationRepository.deleteByUserId(idUser);
        return ResponseDTO.builder().message("Registro eliminado").build();
    }
    public ResponseDTO _GetInforCompanyByUser(String idUser) {
        Optional<InfoCompany> infoCompany = infoCompanyRepository.findByIdUser(idUser);
        return ResponseDTO.builder().items(new InfoCompanyDTO(infoCompany.get())).build();
    }
    public ResponseDTO _UpdateInfoCompany(InfoCompanyDTO infoCompanyDTO) {
        Optional<InfoCompany> infoCompany = infoCompanyRepository.findByIdUser(infoCompanyDTO.idUser);
        if(infoCompany.isPresent()) {
            infoCompany.orElseThrow().setGeneralDescription(infoCompanyDTO.generalDescription);
            infoCompany.orElseThrow().setCompanyName(infoCompanyDTO.companyName);
            infoCompany.orElseThrow().setCompanyPicture(infoCompanyDTO.companyPicture);
            infoCompany.orElseThrow().setFacebook(infoCompanyDTO.facebook);
            infoCompany.orElseThrow().setInstagram(infoCompanyDTO.instagram);
            infoCompany.orElseThrow().setWebPage(infoCompanyDTO.webPage);
            infoCompanyRepository.save(infoCompany.get());
        } else {
            infoCompanyRepository.save(new InfoCompany(infoCompanyDTO));
        }
        return ResponseDTO.builder().message("Información de negocio guardado con éxito").build();
    }
}
