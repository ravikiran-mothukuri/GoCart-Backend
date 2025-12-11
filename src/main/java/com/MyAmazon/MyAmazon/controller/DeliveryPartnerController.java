package com.MyAmazon.MyAmazon.controller;

import com.MyAmazon.MyAmazon.model.DeliveryPartner;
import com.MyAmazon.MyAmazon.service.DeliveryPartnerService;
import com.MyAmazon.MyAmazon.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173/")
@RequestMapping("/api/delivery")
public class DeliveryPartnerController {

    private DeliveryPartnerService deliveryPartnerService;
    private JwtUtil jwtUtil;

    public DeliveryPartnerController(DeliveryPartnerService deliveryPartnerService, JwtUtil jwtUtil){
        this.deliveryPartnerService= deliveryPartnerService;
        this.jwtUtil= jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody DeliveryPartner deliveryPartner){
        return ResponseEntity.ok(deliveryPartnerService.register(deliveryPartner));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody DeliveryPartner deliveryPartner){
        String token= deliveryPartnerService.login(deliveryPartner);
        DeliveryPartner dbPartner= deliveryPartnerService.getByUsername(deliveryPartner.getUsername());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "id", dbPartner.getId(),
                "username", dbPartner.getUsername(),
                "status", dbPartner.getStatus()
        ));
    }

    @PutMapping("/updateLocation")
    public ResponseEntity<?> updateLocation(@RequestHeader("Authorization") String authHeader,@RequestBody Map<String,Double> body){
        String token= authHeader.replace("Bearer ","");
        String username= jwtUtil.extractUserName(token);

        double lat= body.get("lat");
        double lon= body.get("lon");

        DeliveryPartner partner= deliveryPartnerService.updateLocation(username,lat,lon);
        return ResponseEntity.ok(Map.of("message", "Location updated", "partner", partner));
    }

    @PutMapping("/status")
    public ResponseEntity<?> updateStatus(@RequestHeader("Authorization") String authHeader, @RequestBody Map<String, String> body){
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUserName(token);

        String status = body.get("status");

        DeliveryPartner partner= deliveryPartnerService.updateStatus(username,status);
        return ResponseEntity.ok(Map.of("status", partner.getStatus()));
    }

    @PutMapping("/order/picked/{orderId}")
    public ResponseEntity<?> markPicked(@RequestHeader("Authorization") String authHeader, @PathVariable Integer orderId) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUserName(token);

        deliveryPartnerService.markOrderPicked(username, orderId);
        return ResponseEntity.ok(Map.of("message", "Order picked"));
    }

    @PutMapping("/order/delivered/{orderId}")
    public ResponseEntity<?> markDelivered(@RequestHeader("Authorization") String authHeader, @PathVariable Integer orderId) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUserName(token);

        deliveryPartnerService.markDelivered(username, orderId);
        return ResponseEntity.ok(Map.of("message", "Order delivered"));
    }

    @GetMapping("/current-order")
    public ResponseEntity<?> getCurrentOrder(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUserName(token);

        DeliveryPartner partner = deliveryPartnerService.getByUsername(username);

        return ResponseEntity.ok(Map.of(
                "currentOrderId", partner.getCurrentOrderId()
        ));
    }


}
