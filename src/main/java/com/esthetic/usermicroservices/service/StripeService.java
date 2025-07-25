package com.esthetic.usermicroservices.service;

import com.esthetic.usermicroservices.dto.ResponseDTO;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.EphemeralKey;
import com.stripe.model.PaymentIntent;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.EphemeralKeyCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StripeService {
    @Value("${stripe.publickey}")
    public String publickKey;
    @Value("${stripe.secretkey}")
    public String secretKey;

    public ResponseDTO _GetClientIdStripe(){
        return ResponseDTO.builder().items(publickKey).build();
    }
    public ResponseDTO _MakeOrder(Long amount, String email, String name) throws StripeException {
        Stripe.apiKey = secretKey;
        Long amountCents = amount * 100;

        CustomerCreateParams customerParams = CustomerCreateParams.builder().setEmail(email).setName(name).build();
        Customer customer = Customer.create(customerParams);
        EphemeralKeyCreateParams ephemeralKeyParams =
                EphemeralKeyCreateParams.builder()
                        .setStripeVersion("2025-06-30.basil")
                        .setCustomer(customer.getId())
                        .build();

        EphemeralKey ephemeralKey = EphemeralKey.create(ephemeralKeyParams);
        PaymentIntentCreateParams paymentIntentParams =
                PaymentIntentCreateParams.builder()
                        .setAmount(amountCents)
                        .setCurrency("mxn")
                        .setCustomer(customer.getId())
                        // In the latest version of the API, specifying the `automatic_payment_methods` parameter
                        // is optional because Stripe enables its functionality by default.
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();

        PaymentIntent paymentIntent = PaymentIntent.create(paymentIntentParams);

        Map<String, String> responseData = new HashMap<>();
        responseData.put("paymentIntent", paymentIntent.getClientSecret());
        responseData.put("ephemeralKey", ephemeralKey.getSecret());

        responseData.put("customer", customer.getId());
        responseData.put("publishableKey", publickKey);
        return ResponseDTO.builder().items(responseData).build();
    }

}
