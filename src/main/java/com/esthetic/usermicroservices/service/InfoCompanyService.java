package com.esthetic.usermicroservices.service;

import com.esthetic.usermicroservices.dto.InfoCompanyDTO;
import com.esthetic.usermicroservices.dto.PageDTO;
import com.esthetic.usermicroservices.dto.ResponseDTO;
import com.esthetic.usermicroservices.repository.InfoCompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InfoCompanyService {
    private final InfoCompanyRepository infoCompanyRepository;
    public ResponseDTO _GetProviderAvailable(int page, int size, String typeService, String word) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Object[]> listProvider = null;
        if(typeService != null && !typeService.isEmpty()){
            Integer[] arrayTypeService = Arrays.stream(typeService.split(","))
                    .map(Integer::parseInt)
                    .toArray(Integer[]::new);

            listProvider = infoCompanyRepository.getProviderByTypeServices(word, arrayTypeService, pageRequest);
        } else {
            listProvider = infoCompanyRepository.getProvider(word, pageRequest);
        }
        List<InfoCompanyDTO> listUserDto = new ArrayList<>();
        for(Object[] item : listProvider){
            InfoCompanyDTO infoCompanyDTO = new InfoCompanyDTO();
            infoCompanyDTO.idUser = (String) item[0];
            infoCompanyDTO.id = (Long) item[1];
            infoCompanyDTO.companyName = (String) item[2];
            infoCompanyDTO.generalDescription = (String) item[3];
            infoCompanyDTO.companyPicture = (String)item[4];
            infoCompanyDTO.typesServices = (String) item[5];
            listUserDto.add(infoCompanyDTO);
        }
        PageDTO pageDTO = new PageDTO(listProvider);
        pageDTO.items = listUserDto;

        return ResponseDTO.builder().items(pageDTO).build();
    }
}
