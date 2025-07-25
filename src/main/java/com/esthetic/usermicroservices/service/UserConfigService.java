package com.esthetic.usermicroservices.service;

import com.esthetic.usermicroservices.dto.*;
import com.esthetic.usermicroservices.entity.InfoCompany;
import com.esthetic.usermicroservices.entity.Training;
import com.esthetic.usermicroservices.entity.User;
import com.esthetic.usermicroservices.entity.UserLocation;
import com.esthetic.usermicroservices.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserConfigService {
    private final UserLocationRepository userLocationRepository;
    private final UserRepository userRepository;
    private final UserPlanRepository userPlanRepository;
    private final InfoCompanyRepository infoCompanyRepository;
    private final TrainingRepository trainingRepository;
    public ResponseDTO _GetTraining() {
        List<Training> trainingList = trainingRepository.findAll();
        return ResponseDTO.builder().items(trainingList.stream().map(item -> new TrainingDTO(item)).collect(Collectors.toList())).build();
    }
    public ResponseDTO _GetLocationByUser(String idUser) {
        Optional<UserLocation> userLocation = userLocationRepository.findByIdUser(idUser);
        if(userLocation.isPresent()) {
            return ResponseDTO.builder().error(false).items(
                    new UserLocationDTO()._GetInfoLocation(userLocation.get())
            ).build();
        } else {
            return ResponseDTO.builder().error(true).message("No se encontro una ubicación guardada").build();
        }
    }
    public ResponseDTO _GetDefaultLocationByUser(String idUser) {
        Optional<User> user = userRepository.findById(idUser);
        if(user.isPresent()) {
            Map<String, String> userLocationDefault = new HashMap<>();
            userLocationDefault.put("defaultState", user.get().getDefaultState());
            userLocationDefault.put("defaultMunicipality", user.get().getDefaultMunicipality());
            return ResponseDTO.builder().items(userLocationDefault).build();
        }
        return ResponseDTO.builder().error(true).message("No se encontro el usuario").build();
    }
    public ResponseDTO _SaveDefaultLocationClient(String idUser, String defaultState, String defaultMunicipality) {
        Optional<User> user = userRepository.findById(idUser);
        if(user.isPresent()) {
            user.orElseThrow().setDefaultState(defaultState);
            user.orElseThrow().setDefaultMunicipality(defaultMunicipality);
            userRepository.save(user.get());
            return ResponseDTO.builder().message("Ubicacion pretederminada guardada con éxito").build();
        }
        return ResponseDTO.builder().error(true).message("No se encontro el usuario").build();
    }
    public ResponseDTO _SaveUserLocation(UserLocationDTO userLocationDTO) {
        Optional<UserLocation> userLocation = userLocationRepository.findByIdUser(userLocationDTO.getIdUser());
        if(userLocation.isPresent()) {
            userLocation.orElseThrow().setLatitude(userLocationDTO.getLatitude());
            userLocation.orElseThrow().setLongitude(userLocationDTO.getLongitude());
            userLocation.orElseThrow().setIdState(userLocationDTO.idState);
            userLocation.orElseThrow().setIdMunicipality(userLocationDTO.idMunicipality);
            userLocation.orElseThrow().setAuxState(userLocationDTO.auxState);
            userLocation.orElseThrow().setAuxMunicipality(userLocationDTO.auxMunicipality);
            userLocation.orElseThrow().setReference(userLocationDTO.reference);
            userLocationRepository.save(userLocation.get());
        } else {
            UUID uuid = UUID.randomUUID();
            userLocationDTO.setId(uuid.toString());
            userLocationDTO.userDTO = new UserDTO(new User(userLocationDTO.idUser));
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
        Optional<UserPlanDTO> userPlanDTO = userPlanRepository.findByIdUserOrderByStartDateASC(idUser).stream().findFirst();
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
            infoCompanyDTO.userDTO = new UserDTO(new User(infoCompanyDTO.idUser));
            infoCompanyRepository.save(new InfoCompany(infoCompanyDTO));
        }
        return ResponseDTO.builder().message("Información de negocio guardado con éxito").build();
    }
    public ResponseDTO _SaveNotificationToken(String idUser, String notificationToken) {
        Optional<User> user = userRepository.findById(idUser);
        if(user.isPresent()) {
            user.orElseThrow().setTokenNotification(notificationToken);
            userRepository.save(user.get());
            return ResponseDTO.builder().message("Token de notificaciones push guardado con éxito").build();
        }
        return ResponseDTO.builder().message("No se encontro el usuario").build();
    }
}
