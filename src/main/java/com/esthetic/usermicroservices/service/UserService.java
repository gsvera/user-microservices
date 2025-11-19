package com.esthetic.usermicroservices.service;

import com.esthetic.usermicroservices.clases.RequestTokenReset;
import com.esthetic.usermicroservices.config.EnvConfig;
import com.esthetic.usermicroservices.dto.*;
import com.esthetic.usermicroservices.entity.*;
import com.esthetic.usermicroservices.repository.*;
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
import java.time.*;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final SendMailService sendMailService;
    private final ResetTokenService resetTokenService;
    private final CatalogPlanRepository catalogPlanRepository;
    private final UserPlanRepository userPlanRepository;
    private final PaymentService paymentService;
    private final ApiHelper apiHelper;
    private final UserConfigService userConfigService;
    private final InfoCompanyRepository infoCompanyRepository;
    private final FavoriteProviderRepository favoriteProviderRepository;
    private final PushNotificationService pushNotificationService;
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
        this._SendWelcomeToMeredithAestheticWork(newUser.getEmail(), newUser.getFirstName()+ " " + newUser.getLastName());

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
        Thread.sleep(500); // <- solo para probar
        this._SendWelcomeToMeredithAesthetic(newUser.getEmail(), newUser.getFirstName() + " " + newUser.getLastName());

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
                    "                   <a class=\"btn-success\" href=\""+envConfig.getHostname()+"/api/esthetic/user/account-verification?account="+idUser+"\">Verificar cuenta</a> \n"+
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

    public void _SendWelcomeToMeredithAesthetic(String email, String nameClient) throws MessagingException {
        try {
            String htmlBody = "<!DOCTYPE html>\n" +
                    "                <html lang=\"en\">\n" +
                    "                <head>\n" +
                    "                    <meta charset=\"UTF-8\">\n" +
                    "                    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "                    <title>MeCare</title>\n" +
                    "                    <style>\n" +
                    "                        @media(max-width: 520px) {\n" +
                    "                            .card {\n" +
                    "                                width: 90%;\n" +
                    "                            }\n" +
                    "                        }\n" +
                    "                        @media(min-width: 520px) {\n" +
                    "                            .card {\n" +
                    "                                width: 50%;\n" +
                    "                            }\n" +
                    "                        }\n" +
                    "                        .card {\n" +
                    "                            border-radius: 10px;\n" +
                    "                            margin: 0 auto;\n" +
                    "                            padding: 20px;\n" +
                    "                            box-shadow: 0 0 11px -3px rgba(0,0,0,0.8);\n" +
                    "                        }\n" +
                    "                        .text {\n" +
                    "                            font-family:Arial, Helvetica, sans-serif ;\n" +
                    "                            text-align: center;\n" +
                    "                        }\n" +
                    "                        .content-logo {\n" +
                    "                            display: flex;\n" +
                    "                            justify-content: center;;\n" +
                    "                        }\n" +
                    "                        .img-logo{\n" +
                    "                            width: 200px;\n" +
                    "                        }\n" +
                    "                        .t-center {\n" +
                    "                            text-align: center;\n" +
                    "                        }\n" +
                    "                        .t-justify {\n" +
                    "                            text-align: justify;\n" +
                    "                        }\n" +
                    "                    </style>\n" +
                    "                </head>\n" +
                    "                <body>\n" +
                    "                    <div class=\"card\">\n" +
                    "                        <div class=\"content-logo\">\n" +
                    "                            <img class=\"img-logo\" src=\"https://meredith-aesthetic.com/meredith-text-logo.png\"/>\n" +
                    "                        </div>\n" +
                    "                        <h4 class=\"text\">Bienvenido a Meredith Aesthetic</h4>\n" +
                    "                        <p>\n" +
                    "                            Hola " + nameClient + ",\n" +
                    "                        </p>\n" +
                    "                        <p>¡Gracias por registrarte en Meredith Aesthetic!</p>\n" +
                    "                        <p class=\"t-justify\">Nos da mucho gusto darte la bienvenida a nuestra comunidad. A partir de hoy, tendrás acceso a una plataforma diseñada para brindarte una experiencia moderna, segura y personalizada en el mundo de la estética, la belleza y el bienestar.</p>\n" +
                    "                        <p>En Meredith Aesthetic podrás:</p>\n" +
                    "                        <ul>\n" +
                    "                            <li>Encontrar a los mejores profesionales cerca de ti.</li>\n" +
                    "                            <li>Revisar sus calificaciones, trabajos y recomendaciones.</li>\n" +
                    "                            <li>Agendar tus citas de forma rápida, fácil y segura.</li>\n" +
                    "                            <li>Disfrutar una experiencia pensada completamente para ti.</li>\n" +
                    "                        </ul>\n" +
                    "                        <p class=\"t-justify\">Estamos aquí para acompañarte en cada paso y asegurarnos de que tu experiencia sea siempre la mejor.</p>\n" +
                    "                        <p class=\"t-justify\">Si tienes alguna duda o necesitas ayuda, nuestro equipo de soporte estará encantado de asistirte.</p>\n" +
                    "                        <p class=\"t-center\">Gracias por confiar en nosotros</p>\n" +
                    "                        <p class=\"t-center\">\n" +
                    "                El equipo de Meredith Aesthetic\n" +
                    "                        </p>\n" +
                    "                    </div>\n" +
                    "                </body>\n" +
                    "                </html>";

            sendMailService.sendEmail(email, "¡Bienvenido(a) a Meredith Aesthetic!", htmlBody);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void _SendWelcomeToMeredithAestheticWork(String email, String nameClient) throws MessagingException {
        try {
            String htmlBody = "<!DOCTYPE html>\n" +
                    "                <html lang=\"en\">\n" +
                    "                <head>\n" +
                    "                    <meta charset=\"UTF-8\">\n" +
                    "                    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "                    <title>MeCare</title>\n" +
                    "                    <style>\n" +
                    "                        @media(max-width: 520px) {\n" +
                    "                            .card {\n" +
                    "                                width: 90%;\n" +
                    "                            }\n" +
                    "                        }\n" +
                    "                        @media(min-width: 520px) {\n" +
                    "                            .card {\n" +
                    "                                width: 50%;\n" +
                    "                            }\n" +
                    "                        }\n" +
                    "                        .card {\n" +
                    "                            border-radius: 10px;\n" +
                    "                            margin: 0 auto;\n" +
                    "                            padding: 20px;\n" +
                    "                            box-shadow: 0 0 11px -3px rgba(0,0,0,0.8);\n" +
                    "                        }\n" +
                    "                        .text {\n" +
                    "                            font-family:Arial, Helvetica, sans-serif ;\n" +
                    "                            text-align: center;\n" +
                    "                        }\n" +
                    "                        .content-logo {\n" +
                    "                            display: flex;\n" +
                    "                            justify-content: center;;\n" +
                    "                        }\n" +
                    "                        .img-logo{\n" +
                    "                            width: 200px;\n" +
                    "                        }\n" +
                    "                        .t-center {\n" +
                    "                            text-align: center;\n" +
                    "                        }\n" +
                    "                        .t-justify {\n" +
                    "                            text-align: justify;\n" +
                    "                        }\n" +
                    "                    </style>\n" +
                    "                </head>\n" +
                    "                <body>\n" +
                    "                    <div class=\"card\">\n" +
                    "                        <div class=\"content-logo\">\n" +
                    "                            <img class=\"img-logo\" src=\"https://meredith-aesthetic.com/meredith-text-logo.png\"/>\n" +
                    "                        </div>\n" +
                    "                        <h4 class=\"text\">Bienvenido a Meredith Aesthetic</h4>\n" +
                    "                        <p>\n" +
                    "                            Hola " + nameClient + ",\n"+
                    "                        </p>\n" +
                    "                        <p>¡Gracias por unirte a Meredith Aesthetic!</p>\n" +
                    "                        <p class=\"t-justify\">Es un honor darte la bienvenida como profesional de la estética, belleza o bienestar dentro de nuestra plataforma. Desde hoy, cuentas con un espacio creado especialmente para impulsar tu crecimiento, destacarte entre la competencia y conectar con nuevos clientes.</p>\n" +
                    "                        <p>En Meredith Aesthetic podrás:</p>\n" +
                    "                        <ul>\n" +
                    "                            <li>Mostrar tu trabajo con fotos, precios y servicios</li>\n" +
                    "                            <li>Aumentar tu visibilidad y atraer clientes interesados en lo que ofreces.</li>\n" +
                    "                            <li>Gestionar tus citas y agenda de forma sencilla y profesional.</li>\n" +
                    "                            <li>Recibir notificaciones en tiempo real de nuevas reservas.</li>\n" +
                    "                            <li>Construir una reputación sólida con valoraciones auténticas.</li>\n" +
                    "                        </ul>\n" +
                    "                        <p class=\"t-justify\">Nuestro compromiso es ayudarte a hacer crecer tu negocio mientras te enfocas en lo que mejor sabes hacer: brindar un servicio de calidad.</p>\n" +
                    "                        <p class=\"t-justify\">Si necesitas apoyo para completar tu perfil o tienes dudas, nuestro equipo está siempre listo para ayudarte.</p>\n" +
                    "                        <p class=\"t-center\">¡Bienvenido(a) a una nueva etapa profesional!</p>\n" +
                    "                        <p class=\"t-center\">\n" +
                    "                El equipo de Meredith Aesthetic\n" +
                    "                        </p>\n" +
                    "                    </div>\n" +
                    "                </body>\n" +
                    "                </html>";
            sendMailService.sendEmail(email, "¡Bienvenido(a) a Meredith Aesthetic!", htmlBody);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
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
    public void _SendNotificationToPrevEndPlan() {
        Instant startOfTomorrow = LocalDate.now(ZoneOffset.UTC)
                .plusDays(1)
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC);

        Instant endOfTomorrow = LocalDate.now(ZoneOffset.UTC)
                .plusDays(1)
                .atTime(LocalTime.MAX)
                .toInstant(ZoneOffset.UTC);
        List<UserPlan> lisUserPlan = userPlanRepository.findPlansEndingTomorrow(startOfTomorrow, endOfTomorrow);

        for(UserPlan up: lisUserPlan) {
            Optional<User> user = userRepository.findById(up.getIdUser());
            if(user.isPresent()) {
                if(user.get().getTokenNotification() != null && !user.get().getTokenNotification().equals("")) {
                    pushNotificationService.sendPushNotification(user.get().getTokenNotification(), "Recordatorio de pago", "Tu suscripción en Meredith Aesthetic esta por vencer. Realiza tu pago para seguir disfrutando de todas las funcionalidades de la app." );
                }
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
    public ResponseDTO _GetFavoritesKeysProvider(String idClient) {
         List<String> listKeys = favoriteProviderRepository.findKeysFavoriteProvider(idClient);
         return ResponseDTO.builder().items(listKeys).build();
    }
    public ResponseDTO _SaveFavoriteProvider(FavoriteProviderDTO favoriteProviderDTO){
        favoriteProviderRepository.save(new FavoriteProvider(favoriteProviderDTO));
        return ResponseDTO.builder().message("Se guardo en mis favoritos").build();
    }
    public ResponseDTO _DeleteFavoriteProvider(FavoriteProviderDTO favoriteProviderDTO) {
        favoriteProviderRepository.deleteFavoriteProviderByClient(favoriteProviderDTO.idClient, favoriteProviderDTO.idProvider);
        return ResponseDTO.builder().message("Se elimino de mis favoritos").build();
    }
    public ResponseDTO _GetSimpleDateProvider(String idUser) {
        Optional<UserDTO> userDto = userRepository.findUserSimpleData(idUser);
        if(userDto.isPresent()) {
            return ResponseDTO.builder().items(userDto.get()).build();
        }
        return ResponseDTO.builder().error(true).message("No se encontro los datos del usuario").build();
    }
}