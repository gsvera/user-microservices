package com.esthetic.usermicroservices.controller;

import com.esthetic.usermicroservices.clases.RequestTokenReset;
import com.esthetic.usermicroservices.config.EnvConfig;
import com.esthetic.usermicroservices.dto.*;
import com.esthetic.usermicroservices.entity.PendingUser;
import com.esthetic.usermicroservices.service.*;
import com.google.gson.Gson;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/esthetic/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private InfoCompanyService infoCompanyService;
    @Autowired
    private StripeService stripeService;
    @Autowired
    public EnvConfig envConfig;
    @Autowired
    private UserConfigService userConfigService;
    @Autowired
    private PendingUserService pendingUserService;

    @GetMapping("/find-duplicated-user")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO findDuplicatedUser(@RequestParam String email, @RequestParam String phone) {
        ResponseDTO response = new ResponseDTO();
        try{
            return userService.findUser(email, phone);
        } catch(Exception ex) {
            response.error = true;
            response.message = "Ocurrio un error intentelo mas tarde";
        }
        return response;
    }

    @PostMapping("/save/user-sthetic-work")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO SaveUserWorker(@RequestBody UserDTO userDTO) {
        try{
            return userService._SaveUserStheticWork(userDTO);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return  ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }

    @PostMapping("/save/user-sthetic-client")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO SaveUserClient(@RequestBody UserDTO userDTO) {
        try{
            return userService._SaveUserStheticClient(userDTO);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO LoginUser(@RequestBody LoginRequestDTO userLogin) {
        try{
            return userService._Login(userLogin);
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }

    @GetMapping(value = "/account-verification", produces = MediaType.TEXT_HTML_VALUE)
    public String AccountVerification(@RequestParam(name = "account") String idUser){
        try{
            ResponseDTO responseDTO = userService._AccountVerification(idUser);

            return "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>MeCare</title>\n" +
                    "    <style>\n" +
                    "        .card {\n" +
                    "            border-radius: 5px;\n" +
                    "            border: solid 2px #3C3C3C;\n" +
                    "            width: 50%;\n" +
                    "            margin: 0 auto;\n" +
                    "        }\n" +
                    "        .text {\n" +
                    "            font-family:Arial, Helvetica, sans-serif ;\n" +
                    "            text-align: center;\n" +
                    "        }\n" +
                    "        .content-logo {\n" +
                    "            display: flex;\n" +
                    "            justify-content: center;;\n" +
                    "        }\n" +
                    "        .img-logo{\n" +
                    "            width: 200px;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"card\">\n" +
                    "        <div class=\"content-logo\">\n" +
                    "            <img class=\"img-logo\" src=\""+envConfig.getHostname()+"/meredith-aesthetic-logo-icon.png\"/>\n" +
                    "        </div>\n" +
                    "        <h4 class=\"text\">"+responseDTO.message+"</h4>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return "<html><body><h1>Ocurrio un error intentelo mas tarde</h1></body></html>";
        }
    }

    @PostMapping("/send-verification-code")
    public ResponseDTO ResetDefaultPassword(@RequestBody RequestTokenReset requestTokenReset) {
        try{
            return userService._SendVerificationCode(requestTokenReset.email);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }

    @PostMapping("/save-reset-password")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO SaveResetPassword(@RequestBody RequestTokenReset requestData) {

        try{
            return userService._SaveResetPassword(requestData);
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }

    }

    @GetMapping("/get-provider-available")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO GetProviderAvailable(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String typeService,
            @RequestParam(required = false, defaultValue = "") String word,
            @RequestParam(required = false, defaultValue = "") String defaultState,
            @RequestParam(required = false, defaultValue = "") String defaultMunicipality
            ) {
        try{
            return infoCompanyService._GetProviderAvailable(page, size, typeService, word, defaultState, defaultMunicipality);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }

    @GetMapping("/get-provider-by-id/{id-user}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO GetProviderById(@PathVariable(name = "id-user") String idUser) {
        try {
            return  infoCompanyService._GetProviderById(idUser);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
    @GetMapping("/get-client-id-stripe")
    public ResponseDTO GetClientIdStripe() {
        try{
            return stripeService._GetClientIdStripe();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
    @PostMapping("/make-order-stripe")
    public ResponseDTO MakeOrderStripe(@RequestBody Map<String, Object> body) {
        try{
            Long amount = ((Integer)body.get("amount")).longValue();
            String email = (String)body.get("email");
            String name = (String)body.get("name");

            return stripeService._MakeOrder(amount, email, name);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
    @GetMapping("/get-current-version")
    public ResponseDTO GetCurrentVersion(@RequestParam(name = "slug-name") String slugName){
        try{
            return userConfigService._GetCurrentVersion(slugName);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
    @GetMapping("/get-location-by-provider")
    public ResponseDTO getLocationByUser(@RequestParam(name = "id-user") String idUser) {
        try{
            return userConfigService._GetLocationByUser(idUser);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
    @PostMapping("/create-checkout-session")
    public ResponseDTO CreateCheckoutSession(@RequestBody CheckoutStripeParamsDTO checkoutStripeParamsDTO) {
        try{
            return stripeService._CreateCheckoutSession(checkoutStripeParamsDTO);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo más tarde").build();
        }
    }
    @GetMapping("/verify-payment-stripe")
    public ResponseDTO VerifyPaymentStripe(@RequestParam(name = "session-id") String sessionId) {
        try{
            return stripeService._VerifyPayment(sessionId);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
    @GetMapping("/get-my-current-plan/{id-user}")
    public ResponseDTO GetMyCurrentPlan(@PathVariable("id-user") String idUser) {
        try{
            return userConfigService._GetMyCurrentPlan(idUser);
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
    @GetMapping("/get-simple-data-provider/{id-user}")
    public ResponseDTO GetSimpleDataProvider(@PathVariable("id-user") String idUser) {
        try{
            return userService._GetSimpleDateProvider(idUser);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
    @PostMapping("/save-pay-stripe")
    public ResponseDTO SavePayStripe(@RequestBody PaymentPlanDTO paymentPlanDTO) {
        try{
            return  userService._SavePayStripe(paymentPlanDTO);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
}
