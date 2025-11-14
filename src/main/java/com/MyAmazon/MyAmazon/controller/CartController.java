package com.MyAmazon.MyAmazon.controller;

import com.MyAmazon.MyAmazon.dto.CartItemResponse;
import com.MyAmazon.MyAmazon.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/api/cart")
    public ResponseEntity<List<CartItemResponse>> getCart(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(cartService.getCart(token));
    }

    @PostMapping("/api/cart/add/{productId}")
    public ResponseEntity<List<CartItemResponse>> addToCart(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer productId) {

        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(cartService.addToCart(token, productId));
    }

    @PutMapping("/api/cart/update/{productId}/{qty}")
    public ResponseEntity<List<CartItemResponse>> updateQty(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer productId,
            @PathVariable Integer qty) {

        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(cartService.updateQuantity(token, productId, qty));
    }

    @DeleteMapping("/api/cart/remove/{productId}")
    public ResponseEntity<List<CartItemResponse>> remove(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer productId) {

        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(cartService.removeFromCart(token, productId));
    }
}
