package com.esthetic.usermicroservices.controller;

import com.esthetic.usermicroservices.dto.FavoriteProviderDTO;
import com.esthetic.usermicroservices.dto.PaymentPlanDTO;
import com.esthetic.usermicroservices.dto.ResponseDTO;
import com.esthetic.usermicroservices.dto.UserDTO;
import com.esthetic.usermicroservices.service.InfoCompanyService;
import com.esthetic.usermicroservices.service.PaymentService;
import com.esthetic.usermicroservices.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/esthetic/auth-user")
public class UserAuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private InfoCompanyService infoCompanyService;
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO GetUserById(@PathVariable("id") Long id) {
        ResponseDTO response = new ResponseDTO();
        try{
            response.items = userService.GetUserById(id);
        } catch(Exception ex) {
            response.error = true;
            response.message = "Ocurrio un error intentelo mas tarde";
            System.out.println(ex.getMessage());
        }
        return response;
    }
    @GetMapping("/get-data-user")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO GetUserByToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try{
            return userService.GetUserByToken(token);
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }

    @PutMapping("/update-personel-information")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO UpdatePersonalInformation(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody UserDTO userDTO) {
        try{
          return userService.UpdatePersonalInformation(token, userDTO);
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
    @PutMapping("/update-password-by-user")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO UpdatePasswordByUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam(name = "new-password") String newPassword) {
        try{
            return userService._UpdatePassword(token, newPassword);
        } catch(Exception ex){
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO Logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        ResponseDTO response = new ResponseDTO();
        try{
            userService.Logout(token);
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
            response.error = true;
            response.message = "Ocurrio un error intentelo mas tarde";
        }
        return response;
    }
    @DeleteMapping("/delete-account/{id-user}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO DeleteAccount(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable(name = "id-user") String idUser) {
        try{
            return userService._DeleteAccount(idUser, token);
        } catch (Exception ex) {
            return  ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
    @DeleteMapping("/delete-client-account/{id-user}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO DeleteClientAccount(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable(name = "id-user") String idUser) {
        try{
            return userService._DeleteClientAccount(idUser, token);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }

    @GetMapping("/get-verification-account/{id-user}")
    public ResponseDTO GetVerficiationAccount(@PathVariable(name = "id-user") String idUser){
        try{
            return userService._GetVerificationAccount(idUser);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }

    @PostMapping("/resend-verification-account/{id-user}")
    public ResponseDTO ResendVerificationAccount(@PathVariable(name = "id-user") String idUser) {
        try{
          return userService._ResendVerificationAccount(idUser);
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
    @GetMapping("/get-provider-is-active/{id-provider}")
    public ResponseDTO GetProviderIsActive(@PathVariable(name = "id-provider") String idProvider) {
        try{
            return paymentService._GetProviderIsActive(idProvider);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
    @GetMapping("/get-history-pay/{id-provider}")
    public ResponseDTO GetHistoryPay(@PathVariable(name = "id-provider") String idProvider){
        try{
            return paymentService._GetHistoryPay(idProvider);
        } catch (Exception ex){
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
    @PostMapping("/save-favorite-provider")
    public ResponseDTO SaveFavoriteProvider(@RequestBody FavoriteProviderDTO favoriteProviderDTO) {
        try{
            return userService._SaveFavoriteProvider(favoriteProviderDTO);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
    @DeleteMapping("/delete-favorite-provider")
    public ResponseDTO DeleteFavoriteProvider(@RequestBody FavoriteProviderDTO favoriteProviderDTO) {
        try{
            return userService._DeleteFavoriteProvider(favoriteProviderDTO);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
    @GetMapping("/get-keys-favorites-providers/{id-client}")
    public ResponseDTO GetKeysFavoritesProvider(@PathVariable(name = "id-client") String idClient) {
        try{
            return userService._GetFavoritesKeysProvider(idClient);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
    @GetMapping("/get-my-favorites-providers")
    public ResponseDTO GetMyFavoritesProvider(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam String idClient) {
        try{
            return infoCompanyService._FindMyFavoriteProvider(page, size, idClient);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ResponseDTO.builder().error(true).message("Ocurrio un error intentelo mas tarde").build();
        }
    }
}