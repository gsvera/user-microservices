package com.esthetic.usermicroservices.utils;

import com.esthetic.usermicroservices.dto.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ApiHelper {
    public ResponseDTO _RequestedApi(String apiUrl, String method, HttpEntity httpEntity, Boolean isExternal) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpMethod httpMethod = null;
            switch (method) {
                case "GET":
                    httpMethod = HttpMethod.GET;
                    break;
                case "PUT":
                    httpMethod = HttpMethod.PUT;
                    break;
                case "PATCH":
                    httpMethod = HttpMethod.PATCH;
                    break;
                case "POST":
                    httpMethod = HttpMethod.POST;
                    break;
                case "DELETE":
                    httpMethod = HttpMethod.DELETE;
                    break;
                default:
                    httpMethod = HttpMethod.GET;
                    break;
            }
            ResponseEntity<String> responseApi = restTemplate.exchange(apiUrl,httpMethod,httpEntity, String.class);

            if(responseApi.getStatusCode().is2xxSuccessful()) {
                if(isExternal) {
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> jsonMap = mapper.readValue(responseApi.getBody(), Map.class);
                     return ResponseDTO.builder().items(jsonMap).build();
                }
                Gson objGson = new Gson();
                return objGson.fromJson(responseApi.getBody(), ResponseDTO.class);
            } else {
                return ResponseDTO.builder().error(true).message("Status response: "+responseApi.getStatusCode().toString()).items(responseApi.getBody()).build();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Error to request api").build();
        }
    }
}
