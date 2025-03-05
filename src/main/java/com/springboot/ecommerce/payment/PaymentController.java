package com.springboot.ecommerce.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {


    private static final Logger log = LogManager.getLogger(PaymentController.class);

    @PostMapping("/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent() {
        try {
            PaymentIntentCreateParams params =
                    PaymentIntentCreateParams.builder()
                            .setAmount(1000L) // Amount in cents (e.g., 10.00 USD)
                            .setCurrency("usd")
                            .setAutomaticPaymentMethods(
                                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                            .setEnabled(true)  // Enable automatic payment methods
                                            .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER) // Disable redirects
                                            .build()
                            )
                            .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            return ResponseEntity.ok(Map.of(
                    "clientSecret", paymentIntent.getClientSecret(),
                    "paymentIntentId", paymentIntent.getId(),
                    "status", paymentIntent.getStatus()
            ));
        } catch (StripeException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/confirm-payment")
    public ResponseEntity<?> confirmPayment(@RequestParam String paymentIntentId) {
        try {
            // Retrieve the existing PaymentIntent
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

            // Confirm the PaymentIntent
            PaymentIntentConfirmParams confirmParams =
                    PaymentIntentConfirmParams.builder()
                            .setPaymentMethod("pm_card_visa") // Test payment method
                            .build();

            paymentIntent = paymentIntent.confirm(confirmParams);

            return ResponseEntity.ok(Map.of(
                    "paymentIntentId", paymentIntent.getId(),
                    "status", paymentIntent.getStatus()
            ));
        } catch (StripeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/checkout")
    public ResponseEntity<StripeResponse> checkoutProducts() {

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName("Test" +
                                "product")
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("USD")
                        .setUnitAmount(100L)
                        .setProductData(productData)
                        .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams
                        .LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(priceData)
                        .build();

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:8080/success")
                        .setCancelUrl("http://localhost:8080/cancel")
                        .addLineItem(lineItem)
                        .build();

        Session session = null;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            log.error("error: ", e);
        }

        assert session != null;
        return ResponseEntity.ok(StripeResponse
                .builder()
                .status("SUCCESS")
                .message("Payment session created ")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build());
    }

}
