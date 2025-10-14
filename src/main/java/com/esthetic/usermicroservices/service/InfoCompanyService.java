package com.esthetic.usermicroservices.service;

import com.esthetic.usermicroservices.config.EnvConfig;
import com.esthetic.usermicroservices.dto.*;
import com.esthetic.usermicroservices.entity.User;
import com.esthetic.usermicroservices.repository.InfoCompanyRepository;
import com.esthetic.usermicroservices.repository.UserRepository;
import com.esthetic.usermicroservices.utils.ApiHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InfoCompanyService {
    private final InfoCompanyRepository infoCompanyRepository;
    private final UserRepository userRepository;
    private final ApiHelper apiHelper;
    @Autowired
    private EnvConfig envConfig;
    public ResponseDTO _GetProviderAvailable(int page, int size, String typeService, String word, String defaultState, String defaultMunicipality) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.unsorted());
        Page<Object[]> listProvider = null;
        if(typeService != null && !typeService.isEmpty()){
            Integer[] arrayTypeService = Arrays.stream(typeService.split(","))
                    .map(Integer::parseInt)
                    .toArray(Integer[]::new);

            listProvider = infoCompanyRepository.getProviderByTypeServices(word, defaultState, defaultMunicipality, arrayTypeService, pageRequest);
        } else {
            listProvider = infoCompanyRepository.getProvider(word, defaultState, defaultMunicipality, pageRequest);
        }
        List<InfoCompanyDTO> listUserDto = new ArrayList<>();
        for(Object[] item : listProvider){
            InfoCompanyDTO infoCompanyDTO = new InfoCompanyDTO();
            infoCompanyDTO.idUser = (String)item[0];
            infoCompanyDTO.id = (Long) item[1];
            infoCompanyDTO.companyName = (String) item[2];
            infoCompanyDTO.generalDescription = (String) item[3];
            infoCompanyDTO.companyPictureUrl = (String)item[4];
            infoCompanyDTO.typesServices = (String) item[5];
            infoCompanyDTO.auxState = (String) item[6];
            infoCompanyDTO.auxMunicipality = (String) item[7];
            BigDecimal avgRating = (BigDecimal) item[8];
            infoCompanyDTO.auxRating = avgRating == null ? 0 : avgRating.doubleValue();
            listUserDto.add(infoCompanyDTO);
        }
        PageDTO pageDTO = new PageDTO(listProvider);
        pageDTO.items = listUserDto;

        return ResponseDTO.builder().items(pageDTO).build();
    }
    public ResponseDTO _FindMyFavoriteProvider(int page, int size, String idClient) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.unsorted());
        Page<Object[]> listProvider = infoCompanyRepository.getMyFavoritesProviders(idClient, pageRequest);
        List<InfoCompanyDTO> listUserDto = new ArrayList<>();
        for(Object[] item : listProvider) {
            InfoCompanyDTO infoCompanyDTO = new InfoCompanyDTO();
            infoCompanyDTO.id = (Long) item[0];
            infoCompanyDTO.idUser = (String)item[1];
            infoCompanyDTO.companyName = (String) item[2];
            infoCompanyDTO.generalDescription = (String) item[3];
            infoCompanyDTO.companyPictureUrl = (String)item[4];
            infoCompanyDTO.typesServices = (String) item[5];
            infoCompanyDTO.auxState = (String) item[6];
            infoCompanyDTO.auxMunicipality = (String) item[7];
            BigDecimal avgRating = (BigDecimal) item[8];
            infoCompanyDTO.auxRating = avgRating == null ? 0 : avgRating.doubleValue();
            listUserDto.add(infoCompanyDTO);
        }
        PageDTO pageDTO = new PageDTO(listProvider);
        pageDTO.items = listUserDto;

        return ResponseDTO.builder().items(pageDTO).build();
    }
    public ResponseDTO _GetProvidedrById(String idUser) {
        String api = envConfig.getApiGateway() + "/api/esthetic/catalog-type-service/get-types-by-user/"+idUser;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity httpEntity = new HttpEntity<>(headers);
        ResponseDTO responseApiHelper = apiHelper._RequestedApi(api, "GET", httpEntity, false);
        List<Map<String, Object>> responseItems = (List<Map<String, Object>>) responseApiHelper.items;
        String joinName = responseItems.stream().map(item -> (String)item.get("nameType")).collect(Collectors.joining(","));

        Optional<User> user = userRepository.findUserProviderWithDetails(idUser);
        UserDTO userDTO = new UserDTO(user, true);
        userDTO.typeServices = joinName;
        if(user.isPresent()) {
            return ResponseDTO.builder().items(
                    userDTO
            ).build();
        }
        return ResponseDTO.builder().error(true).message("No se encontrol el usuario").build();
    }
}
