package com.esthetic.usermicroservices.service;

import com.esthetic.usermicroservices.clases.RequestTokenReset;
import com.esthetic.usermicroservices.dto.*;
import com.google.gson.Gson;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Timestamp;
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
    private final MailService mailService;
    private final ResetTokenService resetTokenService;
    @Value("${url.front}")
    public String urlFront;

    @Autowired
    private CatalogProfileService catalogProfileService;

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

        String token = jwtService.GetToken(newUser);

        userRepository.updateTokenById(newUser.getId(), token);

        ResponseLoginDTO response = new ResponseLoginDTO(jwtService.GetToken(newUser), newUser.getIdProfile());

        return ResponseDTO.builder().items(response).build();
    }
    public User FindUserDuplicate(UserDTO userDto) {
        return userRepository.findByEmailQueryNative(userDto.getEmail(), userDto.getPhone());
    }

    public ResponseDTO Login(LoginRequestDTO loginRequestDTO) {

        User user = userRepository.findByEmail(loginRequestDTO.getUsername()).orElseThrow();

        //      PARA VALIDAR QUE EL PASSWORD SEA EL MISMO
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean passwordsMatch = encoder.matches(loginRequestDTO.password, user.getPassword());

        if(passwordsMatch) {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword()));
            String token = jwtService.GetToken(user);

            userRepository.updateTokenById(user.getId(),token);
            ResponseLoginDTO response = new ResponseLoginDTO(token, user.getIdProfile());

            return ResponseDTO.builder().items(response).build();
        } else {
            return ResponseDTO.builder().error(true).message("Pass invalid").build();
        }
    }

    public UserDTO GetUserById(Long id) {
        Optional<User> user = userRepository.findById(id);

        UserDTO userdto = new UserDTO(user);
        if(userdto.getIdProfile() != 0){
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = "http://localhost:8002/api/esthetic/catalog-profile/"+userdto.getIdProfile();
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Gson gson = new Gson();
//                String responseBody = response.getBody();
                ResponseDTO responseService = gson.fromJson(response.getBody(), ResponseDTO.class);
                CatalogProfileDTO catalogProfileDTO = gson.fromJson(responseService.items.toString(), CatalogProfileDTO.class);
                userdto.setCatalogProfileDTO(catalogProfileDTO);
            } else {
                System.err.println("Error al obtener los datos. Código de estado: " + response.getStatusCodeValue());
            }
        }
        return userdto;
    }

    public UserDTO GetUserByToken (String token) {
        Optional<User> user = userRepository.findByToken(token.substring(7));

        UserDTO userDto = new UserDTO(user);

        if(userDto.getId() != null) {
            userDto.setCatalogProfileDTO(catalogProfileService.getById(userDto.getIdProfile()));
        }

        return userDto;
    }

    public ResponseDTO SendResetPassword(RequestTokenReset requestData) throws MessagingException {
        ResponseDTO response = new ResponseDTO();

        Optional<User> user = userRepository.findByEmail(requestData.email);

        if(!user.isEmpty()) {
            String token = resetTokenService.GenerateToken(requestData.email);
            String htmlBody = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <style>\n" +
                    "        .card-password {\n" +
                    "            width: 450px;\n" +
                    "            justify-content: center;\n" +
                    "            display: flex;\n" +
                    "            align-items: center;\n" +
                    "            padding: 10px !important;\n" +
                    "            margin: auto;\n" +
                    "        }\n" +
                    "        .card-form-white-pink {\n" +
                    "            box-shadow: 0 0 4px 2px rgba(180, 58, 107, 0.8);\n" +
                    "            border-radius: 3px;\n" +
                    "        }\n" +
                    "        .btn-success{\n" +
                    "            position: relative;\n" +
                    "            z-index: 1;\n" +
                    "            text-align: center;\n" +
                    "            padding: 15px 25px;\n" +
                    "            background: linear-gradient(to right, rgba(180, 58, 107, 0.8), rgba(180, 58, 107, 0.8));\n" +
                    "            color: #fff;\n" +
                    "            display: inline-block;\n" +
                    "            border-radius: 0;\n" +
                    "            border: none;\n" +
                    "            overflow: hidden;\n" +
                    "            text-decoration: none;\n" +
                    "            color: white;\n" +
                    "        }\n" +
                    "        .ii a[href]{\n"+
                    "            color: white;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"card-form-white-pink card-password\">\n" +
                    "        <div class=\"\">\n" +
                    "            <h5>"+requestData.message+"</h5>\n" +
                    "            <div style=\"display: flex; justify-content: center\">\n" +
                    "               <a href=\""+urlFront+"/reset-new-password?token="+token+"\" type=\"button\" class=\"btn-success\" style=\"padding:10px 25px\">Restablecer / Restore</a>\n" +
                    "            </div>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";

            mailService.SendEmail(user.get().getEmail(), "Esthetic Reset password", htmlBody);
            response.error = false;
        } else{
            response.error = true;
        }
        return  response;
    }

    public ResponseDTO SaveResetPassword(RequestTokenReset requestData) {
        ResponseDTO response = new ResponseDTO();
        ResetTokenDTO resetTokenDTO = resetTokenService.GetRecordByToken(requestData.token);

        if(resetTokenDTO.getStatus() == 0) {
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            long differentTime = currentTime.getTime() - resetTokenDTO.getCreateDate().getTime();
            long differentDays = differentTime / (1000 * 60 * 60 * 24);
            if(differentDays > 1) {
                response.error = true;
            } else {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                String encodedPassword = encoder.encode(requestData.password);
                int updatePassword = userRepository.updatePasswordByEmail(encodedPassword, resetTokenDTO.getEmail());
                if(updatePassword > 0) {
                    resetTokenService.UpdateStatus(requestData.token);
                }
            }
        } else {
            response.error = true;
        }
        return response;
    }
}