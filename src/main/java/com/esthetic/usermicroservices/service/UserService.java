package com.esthetic.usermicroservices.service;

import com.esthetic.usermicroservices.clases.RequestTokenReset;
import com.esthetic.usermicroservices.config.EnvConfig;
import com.esthetic.usermicroservices.dto.*;
import com.esthetic.usermicroservices.entity.CatalogPlan;
import com.esthetic.usermicroservices.entity.ResetToken;
import com.esthetic.usermicroservices.entity.UserPlan;
import com.esthetic.usermicroservices.repository.CatalogPlanRepository;
import com.esthetic.usermicroservices.repository.InfoCompanyRepository;
import com.esthetic.usermicroservices.repository.UserPlanRepository;
import com.esthetic.usermicroservices.utils.ApiHelper;
import com.esthetic.usermicroservices.utils.EncrypDecrypCode;
import com.google.gson.Gson;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
    // private final MailService mailService;
    private final SendMailService sendMailService;
    private final ResetTokenService resetTokenService;
    private final CatalogPlanRepository catalogPlanRepository;
    private final UserPlanRepository userPlanRepository;
    private final PaymentService paymentService;
    private final ApiHelper apiHelper;
    private final UserConfigService userConfigService;
    private final InfoCompanyRepository infoCompanyRepository;

    @Autowired
    public EnvConfig envConfig;

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
        newUser.setEmail(objUser.getEmail().toLowerCase());
        newUser.setBirthDate(objUser.getBirthDate());
        newUser.setLada(objUser.getLada());
        newUser.setPhone(objUser.getPhone());
        newUser.setPassword(encodedPassword);
        newUser.setIdProfile(objUser.getIdProfile());
        newUser.setIsProvider(true);
        newUser.setIsClient(true);
        newUser.setActiveProvider(true);
        newUser.setAccountVerification(true);
        newUser.setCreatedAt(objUser.createdAt);
        userRepository.save(newUser);

        PaymentPlanDTO paymentPlanDTO = objUser.paymentPlanDTO;
        paymentPlanDTO.idUser = newUser.getId();
        paymentPlanDTO.paymentDate = objUser.createdAt;
        paymentPlanDTO.createdAt = objUser.createdAt;
        paymentService._CreatePaymentPlan(paymentPlanDTO);

        this._makeUserPlan(Long.valueOf(objUser.getPlanSelect()), objUser.createdAt, newUser.getId());

        String token = jwtService.GetToken(newUser);

        userRepository.updateTokenById(newUser.getId(), token);

        ResponseLoginDTO response = new ResponseLoginDTO(token, newUser);

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
        newUser.setEmail(userDTO.getEmail().toLowerCase());
//        newUser.setBirthDate(objUser.getBirthDate());
        newUser.setLada(userDTO.getLada());
        newUser.setPhone(userDTO.getPhone());
        newUser.setPassword(encodedPassword);
//        newUser.setIdProfile(objUser.getIdProfile());
        newUser.setIsClient(true);
        newUser.setAccountVerification(false);
        newUser.setCreatedAt(userDTO.createdAt);
        userRepository.save(newUser);

        String token = jwtService.GetToken(newUser);

        userRepository.updateTokenById(newUser.getId(), token);

        this._SendVerificationAccount(newUser.getEmail(), newUser.getId());

        ResponseLoginDTO response = new ResponseLoginDTO(token, newUser);

        return ResponseDTO.builder().items(response).build();
    }
    public ResponseDTO UpdatePersonalInformation(String token, UserDTO objUser) {
        Optional<User> user = userRepository.findByToken(token.substring(7));
        if(user.isPresent()) {
            user.orElseThrow().setFirstName(objUser.getFirstName());
            user.orElseThrow().setLastName(objUser.getLastName());
            user.orElseThrow().setPhone(objUser.getPhone());
            user.orElseThrow().setEmail(objUser.getEmail());
            user.orElseThrow().setUpdatedAt(objUser.updatedAt);
            userRepository.save(user.get());
    //                objUser.getBirthDate(), // No se ocupa por ahora
    //                objUser.getLada(), // Se comenta por que por ahora solo es para telefonos locales
            return ResponseDTO.builder().error(false).build();
        }
        return ResponseDTO.builder().error(true).message("No se encontro el usuario").build();

    }
    public ResponseDTO _UpdatePassword(String token, String newPassword) throws Exception {
        Optional<User> user = userRepository.findByToken(token.substring(7));
        if(user.isPresent()) {
            return this._UpdatePasswordByUser(user.get(), newPassword);
        }
        return ResponseDTO.builder().error(true).message("Su session ha vencido, inicie session e intente nuevamente").build();
    }

    public ResponseDTO findUser(String email, String phone) {
        Optional<User> user = Optional.ofNullable(userRepository.findByEmailQueryNative(email, phone)) ;
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
        Optional<User> user = userRepository.findByEmailIgnoreCase(loginRequestDTO.getUsername());

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
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.get().getId(), decryptPass));
                String token = jwtService.GetToken(user.get());

                userRepository.updateTokenById(user.get().getId(),token);
                ResponseLoginDTO response = new ResponseLoginDTO(token, user.get());

                return ResponseDTO.builder().items(response).build();
            }
        }

        return ResponseDTO.builder().error(true).message("Usuario o contraseña invalido").build();
    }
    public ResponseDTO Logout(String token) {
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
            String apiUrl = envConfig.getApiGateway() + "/api/esthetic/catalog-profile/"+userdto.getIdProfile();
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Gson gson = new Gson();
                ResponseDTO responseService = gson.fromJson(response.getBody(), ResponseDTO.class);
                CatalogProfileDTO catalogProfileDTO = gson.fromJson(responseService.items.toString(), CatalogProfileDTO.class);
                userdto.setCatalogProfileDTO(catalogProfileDTO);
            } else {
                System.err.println("Error al obtener los datos. Código de estado: " + response.getStatusCodeValue());
            }
        }
        return userdto;
    }

    public ResponseDTO GetUserByToken (String token) {
        Optional<User> user = userRepository.findByToken(token.substring(7));
        if(user.isPresent()) {
            return ResponseDTO.builder().items(new UserDTO(user.get())).build();
        }
        return ResponseDTO.builder().error(true).message("No se encontro el usuario").build();
    }
    public void _SendVerificationAccount(String email, String idUser) throws MessagingException  {
        try {

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
                    "            background: #D4AF37;\n" +
                    "            display: inline-block;\n" +
                    "            border-radius: 10px;\n" +
                    "            overflow: hidden;\n" +
                    "            text-decoration: none;\n" +
                    "            color: white !important;\n" +
                    "            margin: 0 auto;\n" +
                    "        }\n" +
                    "        .content-btn {\n"+
                    "            display:flex; \n" +
                    "            justify-content: center;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"card-form-white-pink card-password\">\n" +
                    "        <div class=\"\">\n" +
                    "            <h5>Verificación de creación de cuenta Meredith Aesthetic</h5>\n" +
                    "            <div style=\"\">\n" +
                    "               <p>Da click en el siguiente botón para verificar tu nueva cuenta MeCare</p>\n" +
                    "               <div class=\"content-btn\">" +
                    "                   <a class=\"btn-success\" href=\""+envConfig.getApiGateway()+"/api/esthetic/user/account-verification?account="+idUser+"\">Verificar cuenta</a> \n"+
                    "               </div>" +
                    "            </div>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";
            sendMailService.sendEmail(email, "Verificación de cuenta Meredith Aesthetic", htmlBody);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    public ResponseDTO _SendVerificationCode(String email) throws MessagingException {
        try {
            Optional<User> user = userRepository.findByEmailIgnoreCase(email);

            if(user.isPresent()) {
                String codigo = resetTokenService.GenerateToken(email);
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
                        "            <h5>Nuevo código  de verificación</h5>\n" +
                        "            <div style=\"display: flex; justify-content: center\">\n" +
                        "               <p >Tu código  de verificación de Meredith Aesthetic es: <span style=\"font-weight: fold\"> "+codigo+"</span>. No lo compartas con nadie.</p>\n" +
                        "            </div>\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "</body>\n" +
                        "</html>";
                sendMailService.sendEmail(user.get().getEmail(), "Meredith Aesthetic código  de verificación", htmlBody);
                return ResponseDTO.builder().message("Se ha enviado el codigo de verificación a su cuenta de correo").build();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return ResponseDTO.builder().error(true).message("No se encontro el usuario").build();
    }

    public ResponseDTO _AccountVerification(String idUser) {
        Optional<User> user = userRepository.findById(idUser);
        if(user.isPresent()) {
            user.orElseThrow().setAccountVerification(true);
            userRepository.save(user.get());
            return ResponseDTO.builder().message("La cuenta "+ user.get().getEmail()+" fue verificada con éxito." ).build();
        }
        return ResponseDTO.builder().error(true).message("No se encontro la cuenta").build();
    }

    public ResponseDTO _SaveResetPassword(RequestTokenReset requestData) throws Exception {
        Optional<ResetToken> resetToken = resetTokenService.GetRecordByToken(requestData.token);
        if(resetToken.isPresent()) {
            if(resetToken.get().getStatus() == 0 && resetToken.get().getEmail() == resetToken.get().getEmail()) {
                Optional<User> user = userRepository.findByEmailIgnoreCase(requestData.email);
                long elapsedMillis = System.currentTimeMillis() - resetToken.get().getCreateDate().getTime();

                if(elapsedMillis < 60 * 60 * 1000 && user.isPresent()) {
                  ResponseDTO responseDTO = this._UpdatePasswordByUser(user.get(), requestData.password);
                  if(!responseDTO.error) {
                      resetTokenService._DeleteToken(requestData.token);
                      return ResponseDTO.builder().message("Se actualizo la contraseña con éxito").build();
                  } else {
                      return responseDTO;
                  }
                }
            }
        }
        return ResponseDTO.builder().error(true).message("El código de verificación ha caducado").build();
    }

    public ResponseDTO _UpdatePasswordByUser(User user, String newPassword) throws Exception{
        String decryptPass = EncrypDecrypCode.passwordDecrypt(newPassword);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        boolean isPasswordMatch = passwordEncoder.matches(decryptPass, user.getPassword());
        if(isPasswordMatch) {
            return ResponseDTO.builder().error(true).message("La contraseña no puede ser la misma que la anterior").build();
        } else {
            String newPasswordEncode = passwordEncoder.encode(decryptPass);
            userRepository.updatePasswordByEmail(newPasswordEncode, user.getEmail());

            return ResponseDTO.builder().error(false).message("Contraseña actualizada con éxito").build();
        }
    }
    public void _FindUserInactive() {
        List<User> listUser = userRepository.findUserClientInactive();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        long millisecondsIn24Hours = 1000 * 60 * 60 * 24;

        for(User user : listUser) {
            if((currentTime.getTime() - user.getCreatedAt().toEpochMilli()) >= millisecondsIn24Hours) {
                userRepository.deleteUserById(user.getId());
            }
        }
    }

    public void _DisableProviderByEndPlan() {
        Instant today = Instant.now();
        List<UserPlan> userPlanList = userPlanRepository.listPlanExpired(today);

        for(UserPlan us : userPlanList) {
            Optional<UserPlanDTO> userPlanDTO = userPlanRepository.findByIdUserOrderByStartDateASC(us.getIdUser()).stream().findFirst();
            if(userPlanDTO.isPresent()) {
                us.setIsUsed(true);
            }
            us.setIsActive(false);
            userPlanRepository.save(us);
        }
    }
    public ResponseDTO _DeleteAccount(String idUser, String token) {
        Optional<User> user = userRepository.findById(idUser);
        if(user.isPresent() && user.get().getToken().equals(token.substring(7))) {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity httpEntity = new HttpEntity<>(headers);
            headers.set("Authorization", token);
            String apiUrlCatalogs = envConfig.getApiGateway() + "/api/esthetic/delete-user/delete-catalog-account/"+idUser;
            String apiUrlServices = envConfig.getApiGateway() + "/api/esthetic/delete-user-services/delete-services-account/"+idUser;

            apiHelper._RequestedApi(apiUrlCatalogs, "DELETE", httpEntity, false);
            apiHelper._RequestedApi(apiUrlServices, "DELETE", httpEntity, false);

            userConfigService._DeleteLocationByUser(idUser);
            userPlanRepository.deleteAllPlanByUser(idUser);
            infoCompanyRepository.deleteInfoCompany(idUser);
            userRepository.deleteUserById(idUser);
            return ResponseDTO.builder().message("Usuario eliminado completamente").build();
        }
        return ResponseDTO.builder().error(true).message("No se encontro el registro").build();

    }
    public ResponseDTO _DeleteClientAccount(String idUser, String token) {
        Optional<User> user = userRepository.findById(idUser);
        if(user.isPresent() && user.get().getToken().equals(token.substring(7))) {
            if(user.get().getIsProvider() != null && user.get().getIsProvider()) {
                userRepository.removeIsClient(idUser);
                return ResponseDTO.builder().message("Se removio el acceso a cliente").build();
            }
            userRepository.deleteUserById(idUser);
            return ResponseDTO.builder().message("Usuario eliminado completamente").build();
        }
        return ResponseDTO.builder().error(true).message("No se encontro el registro").build();
    }

    public ResponseDTO _GetVerificationAccount(String idUser) {
        Optional<User> user = userRepository.findById(idUser);
        if(user.isPresent()) {
            return ResponseDTO.builder().items(new UserDTO(user.get())._GetVerification()).build();
        }
        return ResponseDTO.builder().error(true).build();
    }

    public ResponseDTO _ResendVerificationAccount(String idUser) throws MessagingException {
        Optional<User> user = userRepository.findById(idUser);
        if(user.isPresent()) {
            this._SendVerificationAccount(user.get().getEmail(), idUser);
            return ResponseDTO.builder().message("Se envio el correo de verificacion a la cuenta email del usuario").build();
        }
        return ResponseDTO.builder().error(true).message("No se encontro el usuario").build();
    }

    public ResponseDTO _SavePayStripe(PaymentPlanDTO paymentPlanDTO) {
        Instant today = Instant.now();
        Instant paymentDate = paymentPlanDTO.paymentDate;
        userPlanRepository.updateIsUsedExpiredByUser(paymentPlanDTO.idUser, today);
        Boolean isError = paymentService._CreatePaymentPlan(paymentPlanDTO).error;
        if(!isError) {
            Optional<UserPlanDTO> userPlanDTO = userPlanRepository.findByIdUserOrderByStartDateDESC(paymentPlanDTO.idUser).stream().findFirst();
            if(userPlanDTO.isPresent()) {
                paymentDate = userPlanDTO.get().endDate;
            }

            this._makeUserPlan(Long.valueOf(paymentPlanDTO.planId), paymentDate, paymentPlanDTO.idUser);
            return ResponseDTO.builder().message("Pago realizado con éxito").build();
        }
        return ResponseDTO.builder().build();
    }
    private void _makeUserPlan(Long idPlan, Instant createdAt, String idUser) {
        Optional<CatalogPlan> catalogPlan = catalogPlanRepository.findById(idPlan);

        ZonedDateTime zoned = createdAt.atZone(ZoneOffset.UTC);
        ZonedDateTime endZoned = zoned.plusMonths(catalogPlan.get().getDuration());
        Instant endDate = endZoned.toInstant();

        UserPlan userPlan = new UserPlan(
                idUser,
                catalogPlan.get(),
                catalogPlan.get().getDuration(),
                createdAt,
                createdAt,
                endDate,
                true,
                false
        );
        userPlanRepository.save(userPlan);
    }
}