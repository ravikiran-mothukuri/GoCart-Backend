package com.Gocart.Gocart.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Gocart.Gocart.dto.WishlistItemResponse;
import com.Gocart.Gocart.model.Product;
import com.Gocart.Gocart.model.User;
import com.Gocart.Gocart.model.WishlistItem;
import com.Gocart.Gocart.repository.ProductRepository;
import com.Gocart.Gocart.repository.UserRepository;
import com.Gocart.Gocart.repository.WishlistItemRepository;
import com.Gocart.Gocart.util.JwtUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WishlistService {
    @Autowired
    private WishlistItemRepository wishlistRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private JwtUtil jwtUtil;

    private Integer getUserId(String token) {
        try {
            String username = jwtUtil.extractUserName(token);
            User user = userRepo.findByUsername(username).orElse(null);
            return user != null ? user.getId() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private WishlistItemResponse toDTO(WishlistItem item, Product product) {
        WishlistItemResponse dto = new WishlistItemResponse();

        dto.setWishlistItemId(item.getId());
        dto.setProductId(item.getProductId());

        if (product != null) {
            dto.setName(product.getName());
            dto.setBrand(product.getBrand());
            dto.setPrice(product.getPrice());
            dto.setCategory(product.getCategory());
            dto.setImageUrl(product.getImageUrl()); // FIXED
        }


        return dto;
    }

    public List<WishlistItemResponse> getWishlist(String token) {
        Integer userId = getUserId(token);
        List<WishlistItem> items = wishlistRepo.findByUserId(userId);

        if (items.isEmpty()) {
            return List.of();
        }

        List<Integer> productIds = items.stream()
                .map(WishlistItem::getProductId)
                .collect(Collectors.toList());

        Map<Integer, Product> productMap = productRepo.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        return items.stream()
                .map(item -> toDTO(item, productMap.get(item.getProductId())))
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> addToWishlist(String token, Integer productId) {
        Integer userId = getUserId(token);
        WishlistItem existing = wishlistRepo.findByUserIdAndProductId(userId, productId);

        Map<String, Object> response = new HashMap<>();

        if (existing != null) {
            response.put("status", "exists");
            response.put("message", "Product already in wishlist");
        } else {
            WishlistItem item = new WishlistItem();
            item.setUserId(userId);
            item.setProductId(productId);
            wishlistRepo.save(item);

            response.put("status", "added");
            response.put("message", "Product added to wishlist");
        }

        response.put("wishlist", getWishlist(token));
        return response;
    }

    @Transactional
    public List<WishlistItemResponse> removeFromWishlist(String token, Integer productId) {
        Integer userId = getUserId(token);

        wishlistRepo.deleteByUserIdAndProductId(userId, productId);
        return getWishlist(token);
    }
}
