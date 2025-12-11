package com.MyAmazon.MyAmazon.controller;

import com.MyAmazon.MyAmazon.model.Order;
import com.MyAmazon.MyAmazon.model.User;
import com.MyAmazon.MyAmazon.repository.UserRepository;
import com.MyAmazon.MyAmazon.service.OrderService;
import com.MyAmazon.MyAmazon.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost/5173")
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
    public Order placeOrder(@RequestHeader("Authorization") String token){
        String Username= jwtUtil.extractUserName(token.replace("Bearer ",""));
        User user= userRepo.findByUsername(Username).orElse(null);
        return orderService.placeOrder(user);
    }
}
