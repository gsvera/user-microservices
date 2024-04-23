package com.esthetic.usermicroservices.service;

import com.esthetic.usermicroservices.dto.*;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import com.esthetic.usermicroservices.repository.UserRepository;
import com.esthetic.usermicroservices.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public ResponseDTO SaveUser(UserDTO objUser) {
        Optional<User> user = Optional.ofNullable(this.FindUserDuplicate(objUser));

        if(user.isPresent()) {
            return ResponseDTO.builder().message("Ya existe un usuario con esos datos").error(true).build();
        }

        UUID uuid = UUID.randomUUID();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(objUser.getPassword());

        User newUser = new User();
        newUser.setId(uuid.toString());
        newUser.setFirstName(objUser.getFirstName());
        newUser.setLastName(objUser.getLastName());
        newUser.setEmail(objUser.getEmail());
        newUser.setBirthDate(objUser.getBirthDate());
        newUser.setPhone(objUser.getPhone());
        newUser.setPassword(encodedPassword);
        newUser.setIdProfile(objUser.getIdProfile());
        userRepository.save(newUser);

        return ResponseDTO.builder().items(jwtService.GetToken(newUser)).build();
    }
    public User FindUserDuplicate(UserDTO userDto) {
        return userRepository.findByEmailQueryNative(userDto.getEmail(), userDto.getPhone());
    }

    public ResponseDTO Login(LoginRequestDTO loginRequestDTO) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword()));
        User user = userRepository.findByEmail(loginRequestDTO.getUsername()).orElseThrow();

        ResponseLoginDTO response = new ResponseLoginDTO(jwtService.GetToken(user), user.getIdProfile());
        return ResponseDTO.builder().items(response).build();
    }

    public UserDTO GetUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        String nameProfile = "";

        UserDTO userdto = new UserDTO(
                user.get().getId(),
                user.get().getFirstName(),
                user.get().getLastName(),
                user.get().getEmail(),
                user.get().getBirthDate(),
                user.get().getPhone(),
                user.get().getIdProfile());
        if(userdto.getIdProfile() != 0){
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = "http://localhost:8002/api/esthetic/catalog-profile/"+userdto.getIdProfile();
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Gson gson = new Gson();
                String responseBody = response.getBody();
                ResponseDTO responseService = gson.fromJson(response.getBody(), ResponseDTO.class);
                CatalogProfileDTO catalogProfileDTO = gson.fromJson(responseService.items.toString(), CatalogProfileDTO.class);
                userdto.setNameProfile(catalogProfileDTO.getProfileName());
            } else {
                System.err.println("Error al obtener los datos. Código de estado: " + response.getStatusCodeValue());
            }
        }
        return userdto;
    }


}


//      PARA VALIDAR QUE EL PASSWORD SEA EL MISMO

//    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//    boolean passwordsMatch = encoder.matches(enteredPassword, passwordFromDatabase);