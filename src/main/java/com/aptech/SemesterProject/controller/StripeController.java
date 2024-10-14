package com.aptech.SemesterProject.controller;

import com.aptech.SemesterProject.exception.CustomRuntimeException;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/checkouts")
public class StripeController {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping("/checkout-session")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody Map<String, Object> requestData) throws CustomRuntimeException {
        Stripe.apiKey = stripeSecretKey;
        System.out.println(System.currentTimeMillis());
        Map<String, Object> tour = (Map<String, Object>) requestData.get("tour");
        Map<String, Object> user = (Map<String, Object>) requestData.get("user");
        Map<String, Object> prevBill = (Map<String, Object>) requestData.get("bill");
//        if (prevBill == null || prevBill.isEmpty()) {
//
//        } else {
//            if (prevBill.get("sessionId") != null) {
//
////                Long creationTime = Long.parseLong(prevBill.get("creationTime").toString());
////                if(creationTime==null||creationTime==0){
////                    throw new CustomRuntimeException("Session expired");
////                }
////                Long currentTime = System.currentTimeMillis();
////                if(currentTime-creationTime>1800000){
//////                    throw new CustomRuntimeException("Session expired");
////
////                }
//                String sessionId = prevBill.get("sessionId").toString();
//                try {
//                    SessionRetrieveParams params = SessionRetrieveParams.builder().build();
//                    Session session = Session.retrieve(sessionId, params, null);
//                    System.out.println("Session retrieved: " + session);
//                    Map<String, String> responseData = new HashMap<>();
//                    responseData.put("sessionId", session.getId());
//                    return ResponseEntity.ok(responseData);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        String successUrl = "http://localhost:5173/user/bookings";
        String cancelUrl = "http://localhost:5173/";

        SessionCreateParams params = SessionCreateParams.builder()
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .setCustomerEmail((String) user.get("email"))

                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount((long)Long.valueOf(prevBill.get("total").toString())*100)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName((String) tour.get("name"))
                                                                .setDescription((String) tour.get("summary"))
                                                                .addImage("https://images.rawpixel.com/image_800/czNmcy1wcml2YXRlL3Jhd3BpeGVsX2ltYWdlcy93ZWJzaXRlX2NvbnRlbnQvbHIvcHUyMzMxNjM2LWltYWdlLWt3dnk3dzV3LmpwZw.jpg")
                                                                .build()
                                                )
                                                .build()
                                )
                                .setQuantity(1L)
                                .build()
                )
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .build();

        try {
            Session session = Session.create(params);
            Map<String, String> responseData = new HashMap<>();
            responseData.put("sessionId", session.getId());
            long creationTime = System.currentTimeMillis();
            responseData.put("creationTime", creationTime + "");
            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            Map<String, String> errorData = new HashMap<>();
            errorData.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorData);
        }
    }
}
