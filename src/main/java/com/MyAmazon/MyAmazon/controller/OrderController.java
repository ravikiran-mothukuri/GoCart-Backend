package com.MyAmazon.MyAmazon.controller;

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




    public OrderController(OrderService orderService, JwtUtil jwtUtil, UserRepository userRepo) {
        this.orderService = orderService;
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;

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
}
