package com.esthetic.usermicroservices.service;

import com.esthetic.usermicroservices.clases.RequestTokenReset;
import com.esthetic.usermicroservices.dto.*;
import com.esthetic.usermicroservices.entity.CatalogPlan;
import com.esthetic.usermicroservices.entity.UserPlan;
import com.esthetic.usermicroservices.repository.CatalogPlanRepository;
import com.esthetic.usermicroservices.repository.UserPlanRepository;
import com.esthetic.usermicroservices.utils.ApiHelper;
import com.esthetic.usermicroservices.utils.EncrypDecrypCode;
import com.google.gson.Gson;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

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
    private final CatalogPlanRepository catalogPlanRepository;
    private final UserPlanRepository userPlanRepository;
    private final PaymentService paymentService;
    private final ApiHelper apiHelper;
    private final UserConfigService userConfigService;
    @Value("${url.front}")
    public String urlFront;

    @Autowired
    private CatalogProfileService catalogProfileService;

    public ResponseDTO _SaveUserStheticWork(UserDTO objUser) throws Exception {
        Optional<User> user = Optional.ofNullable(this.FindUserDuplicate(objUser));

        if(user.isPresent()) {
            return ResponseDTO.builder().message("Ya existe un usuario con esos datos").error(true).build();
        }

        UUID uuid = UUID.randomUUID();
        String decryptPass = EncrypDecrypCode.passwordDecrypt(objUser.getPassword());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(decryptPass);

        User newUser = new User();
        newUser.setId(uuid.toString());
        newUser.setFirstName(objUser.getFirstName());
        newUser.setLastName(objUser.getLastName());
        newUser.setEmail(objUser.getEmail());
        newUser.setBirthDate(objUser.getBirthDate());
        newUser.setLada(objUser.getLada());
        newUser.setPhone(objUser.getPhone());
        newUser.setPassword(encodedPassword);
        newUser.setIdProfile(objUser.getIdProfile());
        newUser.setIsProvider(true);
        newUser.setIsClient(true);
        newUser.setActiveProvider(true);
        userRepository.save(newUser);

        Optional<CatalogPlan> catalogPlan = catalogPlanRepository.findById(Long.valueOf(objUser.getPlanSelect()));
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusMonths(catalogPlan.get().getDuration());
        UserPlan userPlan = new UserPlan(
                newUser.getId(),
                catalogPlan.get(),
                catalogPlan.get().getDuration(),
                Timestamp.from(Instant.now()),
                startDate,
                endDate,
                true
        );

        PaymentPlanDTO paymentPlanDTO = objUser.paymentPlanDTO;
        paymentPlanDTO.idUser = newUser.getId();
        paymentPlanDTO.paymentDate = LocalDateTime.now();
        paymentService._CreatePaymentPlan(paymentPlanDTO);

        userPlanRepository.save(userPlan);

        String token = jwtService.GetToken(newUser);

        userRepository.updateTokenById(newUser.getId(), token);

        ResponseLoginDTO response = new ResponseLoginDTO(jwtService.GetToken(newUser), newUser.getIdProfile(), newUser.getId());

        return ResponseDTO.builder().items(response).build();
    }
    public ResponseDTO _SaveUserStheticClient(UserDTO userDTO) throws Exception {
        Optional<User> user = Optional.ofNullable(this.FindUserDuplicate(userDTO));

        if(user.isPresent()) {
            return ResponseDTO.builder().message("Ya existe un usuario con esos datos").error(true).build();
        }

        UUID uuid = UUID.randomUUID();
        String decryptPass = EncrypDecrypCode.passwordDecrypt(userDTO.getPassword());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(decryptPass);

        User newUser = new User();
        newUser.setId(uuid.toString());
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        newUser.setEmail(userDTO.getEmail());
//        newUser.setBirthDate(objUser.getBirthDate());
        newUser.setLada(userDTO.getLada());
        newUser.setPhone(userDTO.getPhone());
        newUser.setPassword(encodedPassword);
//        newUser.setIdProfile(objUser.getIdProfile());
        newUser.setIsClient(true);
        userRepository.save(newUser);

        String token = jwtService.GetToken(newUser);

        userRepository.updateTokenById(newUser.getId(), token);

        ResponseLoginDTO response = new ResponseLoginDTO(jwtService.GetToken(newUser), newUser.getIdProfile(), newUser.getId());

        return ResponseDTO.builder().items(response).build();
    }
    public ResponseDTO UpdatePersonalInformation(String token, UserDTO objUser) {
        Optional<User> user = userRepository.findByToken(token.substring(7));

        userRepository.updateInformationPersonel(
                objUser.getFirstName(),
                objUser.getLastName(),
//                objUser.getBirthDate(), // No se ocupa por ahora
//                objUser.getLada(), // Se comenta por que por ahora solo es para telefonos locales
                objUser.getPhone(),
                objUser.getEmail(),
                user.get().getId()
        );

        return ResponseDTO.builder().error(false).build();
    }

        public ResponseDTO _UpdatePassword(String token, String newPassword) throws Exception {
        Optional<User> user = userRepository.findByToken(token.substring(7));
        String decryptPass = EncrypDecrypCode.passwordDecrypt(newPassword);
        if(user.isPresent()) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean isPasswordMatch = passwordEncoder.matches(decryptPass, user.get().getPassword());
            if(isPasswordMatch) {
                return ResponseDTO.builder().error(true).message("La contraseña no puede ser la misma que la anterior").build();
            } else {
                String newPasswordEncode = passwordEncoder.encode(decryptPass);
                int updatePassword = userRepository.updatePasswordByEmail(newPasswordEncode, user.get().getEmail());
                if(updatePassword == 0) {
                    return ResponseDTO.builder().error(true).message("Su session ha vencido, inicie session e intente nuevamente").build();
                } else {
                    return ResponseDTO.builder().error(false).message("Contraseña actualizada con éxito").build();
                }
            }
        }
        return ResponseDTO.builder().error(true).message("Su session ha vencido, inicie session e intente nuevamente").build();
    }
    public ResponseDTO findUser(String email, String phone) {
        Optional<User> user = Optional.ofNullable(userRepository.findByEmailQueryNative(email, phone)) ;
        System.out.println("datos encontrados " +user);
        if(user.isPresent()) {
            return ResponseDTO.builder().error(true).message("Ya existe un usuario con esos datos").build();
        }
        else {
            return ResponseDTO.builder().error(false).message("No existe el usuario con los datos proporcionados").build();
        }
    }
    public User FindUserDuplicate(UserDTO userDto) {
        return userRepository.findByEmailQueryNative(userDto.getEmail(), userDto.getPhone());
    }

    public ResponseDTO _Login(LoginRequestDTO loginRequestDTO) throws Exception {
        Optional<User> user = userRepository.findByEmail(loginRequestDTO.getUsername());

        if(user.isPresent()) {
            if(loginRequestDTO.getIsProvider() != null && loginRequestDTO.getIsProvider()) {
                if(!user.get().getIsProvider()) {
                    return ResponseDTO.builder().error(true).message("Acceso denegado, debe adquirir un plan para ingresar como proveedor").build();
                }
            }
            if(loginRequestDTO.getIsClient() != null && loginRequestDTO.getIsClient()) {
                if(!user.get().getIsClient()) {
                    return ResponseDTO.builder().error(true).message("Acceso denegado, no tiene privilegios para ingresar a la aplicación").build();
                }
            }

            //      PARA VALIDAR QUE EL PASSWORD SEA EL MISMO
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String decryptPass = EncrypDecrypCode.passwordDecrypt(loginRequestDTO.password);

            boolean passwordsMatch = encoder.matches(decryptPass, user.get().getPassword());

            if(passwordsMatch) {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), decryptPass));
                String token = jwtService.GetToken(user.get());

                userRepository.updateTokenById(user.get().getId(),token);
                ResponseLoginDTO response = new ResponseLoginDTO(token, user.get().getIdProfile(), user.get().getId());

                return ResponseDTO.builder().items(response).build();
            }
        }

        return ResponseDTO.builder().error(true).message("Usuario o contraseña invalido").build();
    }
    public ResponseDTO Logout(String token) {
        System.out.println(token);
        int updatedRows = userRepository.updateToken(token.substring(7));
        if(updatedRows == 0) {
            return ResponseDTO.builder().error(true).message("No se encontro el usuairo").build();
        }
        return ResponseDTO.builder().error(false).build();
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
        System.out.println(token);
        Optional<User> user = userRepository.findByToken(token.substring(7));

        UserDTO userDto = new UserDTO(user);

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
    public ResponseDTO _DeleteAccount(String idUser, String token) {
        Optional<User> user = userRepository.findById(idUser);
        if(user.isPresent() && user.get().getToken().equals(token.substring(7))) {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity httpEntity = new HttpEntity<>(headers);
            headers.set("Authorization", token);
            String apiUrl = "http://localhost:8002/api/esthetic/delete-user/delete-catalog-account/"+idUser;

            apiHelper._RequestedApi(apiUrl, "DELETE", httpEntity);

            userConfigService._DeleteLocationByUser(idUser);
            userPlanRepository.deleteAllPlanByUser(idUser);
            userRepository.deleteUserById(idUser);
            return ResponseDTO.builder().message("Usuario eliminado completamente").build();
        }
        return ResponseDTO.builder().error(true).message("No se encontro el registro").build();

    }
    public ResponseDTO _DeleteClientAccount(String idUser, String token) {
        Optional<User> user = userRepository.findById(idUser);
        if(user.isPresent() && user.get().getToken().equals(token.substring(7))) {
            if(user.get().getIsProvider()) {
                userRepository.removeIsClient(idUser);
                return ResponseDTO.builder().message("Se removio el acceso a cliente").build();
            }
            userRepository.deleteUserById(idUser);
            return ResponseDTO.builder().message("Usuario eliminado completamente").build();
        }
        return ResponseDTO.builder().error(true).message("No se encontro el registro").build();
    }
}