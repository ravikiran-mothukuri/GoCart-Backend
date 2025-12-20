package com.MyAmazon.MyAmazon.controller;

import com.MyAmazon.MyAmazon.dto.DeliveredOrderDTO;
import com.MyAmazon.MyAmazon.dto.DeliveryEarningsDTO;
import com.MyAmazon.MyAmazon.dto.DeliveryProfileUpdateDTO;
import com.MyAmazon.MyAmazon.dto.OrderTrackingDTO;
import com.MyAmazon.MyAmazon.model.*;
import com.MyAmazon.MyAmazon.repository.OrderItemRepository;
import com.MyAmazon.MyAmazon.repository.UserProfileRepository;
import com.MyAmazon.MyAmazon.service.DeliveryPartnerService;
import com.MyAmazon.MyAmazon.service.OrderHistoryService;
import com.MyAmazon.MyAmazon.service.OrderService;
import com.MyAmazon.MyAmazon.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
// @CrossOrigin(origins = "http://localhost:5173/")
@RequestMapping("/api/delivery")
public class DeliveryPartnerController {

    private DeliveryPartnerService deliveryPartnerService;
    private JwtUtil jwtUtil;
    private final OrderItemRepository orderItemRepository;
    private final UserProfileRepository userProfileRepository;
    private final OrderService orderService;
    private final OrderHistoryService orderHistoryService;

    public DeliveryPartnerController(DeliveryPartnerService deliveryPartnerService, JwtUtil jwtUtil, OrderItemRepository orderItemRepository,UserProfileRepository userProfileRepository,OrderService orderService,OrderHistoryService orderHistoryService){
        this.deliveryPartnerService= deliveryPartnerService;
        this.jwtUtil= jwtUtil;
        this.orderItemRepository= orderItemRepository;
        this.userProfileRepository= userProfileRepository;
        this.orderService= orderService;
        this.orderHistoryService= orderHistoryService;
    }

    // Register a new Delivery Partner.

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

    // Update the delivery profile
    @PutMapping("/update/profile")
    public ResponseEntity<?> updateProfileById(@RequestHeader("Authorization") String authHeader, @RequestBody DeliveryProfileUpdateDTO profileUpdateDTO){
        try{
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUserName(token);

            DeliveryPartner updatedInfo= deliveryPartnerService.updateProfile(username,profileUpdateDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "stats", updatedInfo
            ));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to Update the Delivery Profile."));
        }
    }

    // Add this endpoint to get all assigned orders
    @GetMapping("/orders")
    public ResponseEntity<?> getAssignedOrders(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUserName(token);

            DeliveryPartner partner = deliveryPartnerService.getByUsername(username);

            // Get all orders assigned to this partner (excluding delivered ones)
            List<Order> orders = orderService.getOrdersByDeliveryPartnerId(partner.getId());

            // Transform to include necessary details
            List<Map<String, Object>> orderDetails = orders.stream()
                    .map(order -> {
                        UserProfile userProfile = userProfileRepository.findByUserId(order.getUserId())
                                .orElse(null);

                        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());

                        Map<String, Object> orderMap = new HashMap<>();
                        orderMap.put("id", order.getId());
                        orderMap.put("customer", order.getUsername());
                        orderMap.put("address", userProfile != null ? userProfile.getAddress() : "Address not available");
                        orderMap.put("status", order.getStatus());
                        orderMap.put("items", items.size());
                        orderMap.put("currentOrderId", partner.getCurrentOrderId());
                        orderMap.put("createdAt", order.getCreatedAt());

                        return orderMap;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orders", orderDetails);
            response.put("currentOrderId", partner.getCurrentOrderId()); // null is OK

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        }

    }

    @GetMapping("/order/completed")
    public ResponseEntity<?> getAllOrdersByUserId(@RequestHeader("Authorization") String authHeader){
        try{
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUserName(token); // get the delivery partner username

            DeliveryPartner partner = deliveryPartnerService.getByUsername(username); // the exact delivery man we get the id of him.

            List<DeliveredOrderDTO> orders =
                    orderHistoryService.getDeliveredOrders(partner.getId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "orders", orders
            ));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to fetch completed orders"));
        }
    }

    @GetMapping("/earnings")
    public ResponseEntity<?> getEarnings(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUserName(token);

            DeliveryPartner partner = deliveryPartnerService.getByUsername(username);

            DeliveryEarningsDTO earnings = orderHistoryService.calculateEarnings(partner.getId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "earnings", earnings
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to fetch earnings: " + e.getMessage()));
        }
    }

    @GetMapping("/tracking/{orderId}")
    public ResponseEntity<?> getOrderTracking(@RequestHeader("Authorization") String authHeader, @PathVariable Integer orderId) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUserName(token);

            DeliveryPartner partner = deliveryPartnerService.getByUsername(username);
            Order order = orderService.getOrderById(orderId);

            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Order not found"));
            }

            if (!order.getDeliveryPartnerId().equals(partner.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message", "Not authorized for this order"));
            }

            UserProfile userProfile = userProfileRepository.findByUserId(order.getUserId())
                    .orElseThrow(() -> new RuntimeException("User profile not found"));

            List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

            OrderTrackingDTO tracking = new OrderTrackingDTO();
            tracking.setOrderId(order.getId());
            tracking.setCustomerName(userProfile.getFirstname());
            tracking.setCustomerAddress(userProfile.getAddress());
            tracking.setCustomerMobile(userProfile.getMobile());
            tracking.setCustomerLatitude(userProfile.getCurrentLatitude());
            tracking.setCustomerLongitude(userProfile.getCurrentLongitude());
            tracking.setDeliveryPartnerLatitude(partner.getCurrentLatitude());
            tracking.setDeliveryPartnerLongitude(partner.getCurrentLongitude());
            tracking.setStatus(order.getStatus());
            tracking.setTotalPrice(order.getPrice());
            tracking.setItemCount(items.size());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "tracking", tracking
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to fetch tracking: " + e.getMessage()));
        }
    }

    // Update delivery partner location during delivery
    @PutMapping("/tracking/update-location/{orderId}")
    public ResponseEntity<?> updateDeliveryLocation(@RequestHeader("Authorization") String authHeader,@PathVariable Integer orderId,@RequestBody Map<String, Double> location) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUserName(token);

            double lat = location.get("lat");
            double lon = location.get("lon");

            DeliveryPartner partner = deliveryPartnerService.updateLocation(username, lat, lon);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "location", Map.of(
                            "latitude", partner.getCurrentLatitude(),
                            "longitude", partner.getCurrentLongitude()
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to update location"));
        }
    }


}
