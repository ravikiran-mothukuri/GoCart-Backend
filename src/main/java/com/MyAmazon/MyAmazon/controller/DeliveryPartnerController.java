package com.MyAmazon.MyAmazon.controller;

import com.MyAmazon.MyAmazon.model.DeliveryPartner;
import com.MyAmazon.MyAmazon.service.DeliveryPartnerService;
import com.MyAmazon.MyAmazon.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
// @CrossOrigin(origins = "http://localhost:5173/")
@RequestMapping("/api/delivery")
public class DeliveryPartnerController {

    private DeliveryPartnerService deliveryPartnerService;
    private JwtUtil jwtUtil;

    public DeliveryPartnerController(DeliveryPartnerService deliveryPartnerService, JwtUtil jwtUtil){
        this.deliveryPartnerService= deliveryPartnerService;
        this.jwtUtil= jwtUtil;
    }

    // Register a new Delivery Partner.
//    @PostMapping("/register")
//    public ResponseEntity<?> register(@RequestBody DeliveryPartner deliveryPartner){
//        return ResponseEntity.ok(deliveryPartnerService.register(deliveryPartner));
//    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody DeliveryPartner deliveryPartner) {
        try {
            DeliveryPartner registered = deliveryPartnerService.register(deliveryPartner);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Registration successful");
            response.put("partner", Map.of(
                    "id", registered.getId(),
                    "username", registered.getUsername(),
                    "mobile", registered.getMobile()
            ));

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // login the delivery person.

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody DeliveryPartner deliveryPartner) {
        try {
            String token = deliveryPartnerService.login(deliveryPartner);
            DeliveryPartner dbPartner = deliveryPartnerService.getByUsername(deliveryPartner.getUsername());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "token", token,
                    "id", dbPartner.getId(),
                    "username", dbPartner.getUsername(),
                    "status", dbPartner.getStatus(),
                    "online", dbPartner.getOnline()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody DeliveryPartner deliveryPartner){
//        String token= deliveryPartnerService.login(deliveryPartner);
//        DeliveryPartner dbPartner= deliveryPartnerService.getByUsername(deliveryPartner.getUsername());
//        return ResponseEntity.ok(Map.of(
//                "token", token,
//                "id", dbPartner.getId(),
//                "username", dbPartner.getUsername(),
//                "status", dbPartner.getStatus()
//        ));
//    }

    // get the Delivery person profile.

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUserName(token);

            DeliveryPartner partner = deliveryPartnerService.getByUsername(username);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "partner", partner
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Unauthorized"));
        }
    }

    // Update the Location.

    @PutMapping("/updateLocation")
    public ResponseEntity<?> updateLocation(@RequestHeader("Authorization") String authHeader, @RequestBody Map<String, Double> body) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUserName(token);

            double lat = body.get("lat");
            double lon = body.get("lon");

            DeliveryPartner partner = deliveryPartnerService.updateLocation(username, lat, lon);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Location updated successfully",
                    "location", Map.of(
                            "latitude", partner.getCurrentLatitude(),
                            "longitude", partner.getCurrentLongitude()
                    )
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to update location"));
        }
    }

//    @PutMapping("/updateLocation")
//    public ResponseEntity<?> updateLocation(@RequestHeader("Authorization") String authHeader,@RequestBody Map<String,Double> body){
//        String token= authHeader.replace("Bearer ","");
//        String username= jwtUtil.extractUserName(token);
//
//        double lat= body.get("lat");
//        double lon= body.get("lon");
//
//        DeliveryPartner partner= deliveryPartnerService.updateLocation(username,lat,lon);
//        return ResponseEntity.ok(Map.of("message", "Location updated", "partner", partner));
//    }

    // update the user online status

    @PutMapping("/online/{online}")
    public ResponseEntity<?> updateOnline(@RequestHeader("Authorization") String authHeader, @PathVariable String online) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUserName(token);

            DeliveryPartner partner = deliveryPartnerService.updateOnlineStatus(username, online);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "online", partner.getOnline(),
                    "status", partner.getStatus()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to update status"));
        }
    }

//    @PutMapping("/online/{online}")
//    public ResponseEntity<?> updateOnline(@RequestHeader("Authorization") String authHeader, @PathVariable String online){
//        String token = authHeader.replace("Bearer ", "");
//        String username = jwtUtil.extractUserName(token);
//
//        DeliveryPartner partner= deliveryPartnerService.updateOnlineStatus(username,online);
//        return ResponseEntity.ok(Map.of("online", partner.getOnline()));
//    }

    // picking the order.

    @PutMapping("/order/picked/{orderId}")
    public ResponseEntity<?> markPicked(@RequestHeader("Authorization") String authHeader, @PathVariable Integer orderId) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUserName(token);

            deliveryPartnerService.markOrderPicked(username, orderId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Order marked as picked",
                    "orderId", orderId
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

//    @PutMapping("/order/picked/{orderId}")
//    public ResponseEntity<?> markPicked(@RequestHeader("Authorization") String authHeader, @PathVariable Integer orderId) {
//        String token = authHeader.replace("Bearer ", "");
//        String username = jwtUtil.extractUserName(token);
//
//        deliveryPartnerService.markOrderPicked(username, orderId);
//        return ResponseEntity.ok(Map.of("message", "Order picked"));
//    }

    // delivered of order.
    @PutMapping("/order/delivered/{orderId}")
    public ResponseEntity<?> markDelivered(@RequestHeader("Authorization") String authHeader, @PathVariable Integer orderId) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUserName(token);

            deliveryPartnerService.markDelivered(username, orderId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Order marked as delivered",
                    "orderId", orderId
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

//    @PutMapping("/order/delivered/{orderId}")
//    public ResponseEntity<?> markDelivered(@RequestHeader("Authorization") String authHeader, @PathVariable Integer orderId) {
//        String token = authHeader.replace("Bearer ", "");
//        String username = jwtUtil.extractUserName(token);
//
//        deliveryPartnerService.markDelivered(username, orderId);
//        return ResponseEntity.ok(Map.of("message", "Order delivered"));
//    }

    // Get the status of the current order.

    @GetMapping("/current-order")
    public ResponseEntity<?> getCurrentOrder(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUserName(token);

            DeliveryPartner partner = deliveryPartnerService.getByUsername(username);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "currentOrderId", partner.getCurrentOrderId()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Unauthorized"));
        }
    }

//    @GetMapping("/current-order")
//    public ResponseEntity<?> getCurrentOrder(@RequestHeader("Authorization") String authHeader) {
//        String token = authHeader.replace("Bearer ", "");
//        String username = jwtUtil.extractUserName(token);
//
//        DeliveryPartner partner = deliveryPartnerService.getByUsername(username);
//
//        return ResponseEntity.ok(Map.of(
//                "currentOrderId", partner.getCurrentOrderId()
//        ));
//    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUserName(token);

            Map<String, Object> stats = deliveryPartnerService.getPartnerStats(username);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "stats", stats
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to fetch statistics"));
        }
    }


}
