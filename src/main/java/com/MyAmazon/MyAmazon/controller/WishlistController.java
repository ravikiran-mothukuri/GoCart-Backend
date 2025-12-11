package com.MyAmazon.MyAmazon.controller;

import com.MyAmazon.MyAmazon.dto.WishlistItemResponse;
import com.MyAmazon.MyAmazon.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
// @CrossOrigin(origins = "http://localhost:5173")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;


    @GetMapping("/api/wishlist")
    public ResponseEntity<List<WishlistItemResponse>> getWishlist(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(wishlistService.getWishlist(token));
    }


    @PostMapping("/api/wishlist/add/{productId}")
    public ResponseEntity<Map<String, Object>> addToWishlist(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer productId) {

        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(wishlistService.addToWishlist(token, productId));
    }



    @DeleteMapping("/api/wishlist/remove/{productId}")
    public ResponseEntity<List<WishlistItemResponse>> removeFromWishlist(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer productId) {

        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(wishlistService.removeFromWishlist(token, productId));
    }
}
