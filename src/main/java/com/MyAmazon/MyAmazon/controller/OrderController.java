package com.MyAmazon.MyAmazon.controller;

import com.MyAmazon.MyAmazon.dto.OrderTrackingDTO;
import com.MyAmazon.MyAmazon.model.*;
import com.MyAmazon.MyAmazon.repository.OrderItemRepository;
import com.MyAmazon.MyAmazon.repository.UserProfileRepository;
import com.MyAmazon.MyAmazon.repository.UserRepository;
import com.MyAmazon.MyAmazon.service.DeliveryPartnerService;
import com.MyAmazon.MyAmazon.service.OrderService;
import com.MyAmazon.MyAmazon.service.UserService;
import com.MyAmazon.MyAmazon.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
// @CrossOrigin(origins = "http://localhost/5173")
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepo;
    private final DeliveryPartnerService deliveryPartnerService;
    private final UserProfileRepository userProfileRepository;
    private final OrderItemRepository orderItemRepository;



    public OrderController(OrderService orderService, JwtUtil jwtUtil, UserRepository userRepo,DeliveryPartnerService deliveryPartnerService, UserProfileRepository userProfileRepository,OrderItemRepository orderItemRepository) {
        this.orderService = orderService;
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;
        this.deliveryPartnerService= deliveryPartnerService;
        this.userProfileRepository= userProfileRepository;
        this.orderItemRepository= orderItemRepository;
    }

    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@RequestHeader("Authorization") String authHeader) {

        try {
            String username = jwtUtil.extractUserName(authHeader.replace("Bearer ", ""));
            User user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Order order = orderService.placeOrder(user);
            return ResponseEntity.ok(order);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }



    @GetMapping("/user/orders")
    public ResponseEntity<?> getUserOrders(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUserName(token);

            // Get user by username
            User user = userRepo.findByUsername(username).orElseThrow(()-> new RuntimeException("No User is Found."));

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "User not found"));
            }

            // Get all orders for this user
            List<Order> orders = orderService.getOrdersByUserId(user.getId());

            // Sort by most recent first
            orders.sort((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));

            return ResponseEntity.ok(orders);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to fetch orders: " + e.getMessage()));
        }
    }

    @GetMapping("/order/completed")
    public ResponseEntity<?> getDeliveredOrders(@RequestHeader("Authorization") String authHeader){
        try{
            String token= authHeader.replace("Bearer ","");
            String username= jwtUtil.extractUserName(token);

            DeliveryPartner deliveryPartner= deliveryPartnerService.getByUsername(username);
            if(deliveryPartner==null)
                throw new RuntimeException("Delivery Partner details not Found");

            List<Order> totalOrders= orderService.getOrdersByDeliveryPartnerId(deliveryPartner.getId());

            totalOrders.sort((o1,o2)-> o2.getCreatedAt().compareTo(o1.getCreatedAt()));

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(totalOrders);

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success",false,"message","Please Login first to get the Completed Order Details."));
        }
    }

    @PostMapping("/order/delivered/{orderId}")
    public ResponseEntity<?> deliveredOrder(@RequestHeader("Authorization") String authHeader,@PathVariable("orderId") Integer orderId){
        try{
            String token= authHeader.replace("Bearer ","");
            String username= jwtUtil.extractUserName(token);

            deliveryPartnerService.markDelivered(username, orderId);

            return ResponseEntity.ok(Map.of("success",true));

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success",false,"message","Please Login first to get the Completed Order Details."));
        }
    }

    // Add this endpoint to OrderController.java

    @GetMapping("/tracking/{orderId}")
    public ResponseEntity<?> getOrderTrackingForUser(@RequestHeader("Authorization") String authHeader, @PathVariable Integer orderId) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUserName(token);

            User user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Order order = orderService.getOrderById(orderId);

            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Order not found"));
            }

            if (!order.getUserId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message", "Not authorized for this order"));
            }

            UserProfile userProfile = userProfileRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("User profile not found"));

            DeliveryPartner partner = deliveryPartnerService.getById(order.getDeliveryPartnerId());

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
}
