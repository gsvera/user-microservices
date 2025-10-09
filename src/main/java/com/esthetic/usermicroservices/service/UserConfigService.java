package com.esthetic.usermicroservices.service;

import com.esthetic.usermicroservices.config.EnvConfig;
import com.esthetic.usermicroservices.dto.*;
import com.esthetic.usermicroservices.entity.*;
import com.esthetic.usermicroservices.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final SpaceService spaceService;
    private final EnvConfig envConfig;
    private final ConfigAppMobileRepository configAppMobileRepository;
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
    public ResponseDTO _SaveprofilePicture(String idUser, MultipartFile file) throws IOException {
        try{
            Optional<User> user = userRepository.findById(idUser);
            if(user.isPresent()) {
                String fileName = envConfig.getDirProfile() +"/"+ file.getOriginalFilename();

                if(user.get().getProfilePicture() != null){
                    spaceService.deleteFile(user.get().getProfilePicture());
                }

                String urlProfile = spaceService.uploadFile(fileName, file.getContentType(), file.getInputStream(), file.getSize());
                user.orElseThrow().setProfilePicture(urlProfile);
                userRepository.save(user.get());
                return ResponseDTO.builder().message("Imagen actualizada con éxito").build();
            }
            return ResponseDTO.builder().error(true).message("No se encontro el registro").build();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
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
    public ResponseDTO _UpdateInfoCompany(InfoCompanyDTO infoCompanyDTO, MultipartFile file) throws IOException{
        Optional<InfoCompany> infoCompany = infoCompanyRepository.findByIdUser(infoCompanyDTO.idUser);
        String fileName = envConfig.getDirInfoCompany() +"/"+ file.getOriginalFilename();

        if(infoCompany.isPresent()) {
            if(infoCompany.get().getCompanyPictureUrl() != null) {
                spaceService.deleteFile(infoCompany.get().getCompanyPictureUrl());
            }

            String urlCompanyPicture = spaceService.uploadFile(fileName, file.getContentType(), file.getInputStream(), file.getSize());

            infoCompany.orElseThrow().setGeneralDescription(infoCompanyDTO.generalDescription);
            infoCompany.orElseThrow().setCompanyName(infoCompanyDTO.companyName);
            infoCompany.orElseThrow().setCompanyPictureUrl(urlCompanyPicture);
            infoCompany.orElseThrow().setFacebook(infoCompanyDTO.facebook);
            infoCompany.orElseThrow().setInstagram(infoCompanyDTO.instagram);
            infoCompany.orElseThrow().setWebPage(infoCompanyDTO.webPage);
            infoCompanyRepository.save(infoCompany.get());
        } else {
            String urlCompanyPicture = spaceService.uploadFile(fileName, file.getContentType(), file.getInputStream(), file.getSize());
            infoCompanyDTO.userDTO = new UserDTO(new User(infoCompanyDTO.idUser));
            infoCompanyDTO.companyPictureUrl = urlCompanyPicture;
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
    public ResponseDTO _GetCurrentVersion() {
        Optional<ConfigAppMobile> configAppMobile = configAppMobileRepository.getConfigApp();
        if(configAppMobile.isPresent()) {
            return ResponseDTO.builder().items(new ConfigAppMobileDTO(configAppMobile.get())).build();
        } else {
            return ResponseDTO.builder().error(true).message("No se encontro el registro").build();
        }
    }
}
