package com.MyAmazon.MyAmazon.service;

import com.MyAmazon.MyAmazon.dto.CartItemResponse;
import com.MyAmazon.MyAmazon.model.CartItem;
import com.MyAmazon.MyAmazon.model.Product;
import com.MyAmazon.MyAmazon.model.User;
import com.MyAmazon.MyAmazon.repository.CartItemRepository;
import com.MyAmazon.MyAmazon.repository.ProductRepository;
import com.MyAmazon.MyAmazon.repository.UserRepository;
import com.MyAmazon.MyAmazon.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private JwtUtil jwtUtil;

    private Integer getUserId(String token) {
        if (token == null || token.isBlank()) return null;

        try {
            String username = jwtUtil.extractUserName(token);
            User user = userRepo.findByUsername(username).orElse(null);
            return user != null ? user.getId() : null;
        } catch (Exception e) {
            System.out.println("Invalid JWT: " + token);
            return null;
        }
    }


    private CartItemResponse toDTO(CartItem item) {
        Product product = productRepo.findById((int) item.getProductId()).orElse(null);

        CartItemResponse dto = new CartItemResponse();
        dto.setCartItemId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setQuantity(item.getQuantity());

        if (product != null) {
            dto.setName(product.getName());
            dto.setBrand(product.getBrand());
            dto.setPrice(product.getPrice());
            dto.setCategory(product.getCategory());
            dto.setImageUrl("http://localhost:8080/api/products/" + product.getId() + "/image");
        }

        return dto;
    }

    public List<CartItemResponse> getCart(String token) {
        Integer userId = getUserId(token);
        return cartRepo.findByUserId(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<CartItemResponse> addToCart(String token, Integer productId) {
        Integer userId = getUserId(token);

        CartItem existing = cartRepo.findByUserIdAndProductId(userId, productId);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + 1);
            cartRepo.save(existing);
        } else {
            CartItem item = new CartItem();
            item.setUserId(userId);
            item.setProductId(productId);
            item.setQuantity(1);
            cartRepo.save(item);
        }
        return getCart(token);
    }

    public List<CartItemResponse> updateQuantity(String token, Integer productId, Integer qty) {
        Integer userId = getUserId(token);

        CartItem existing = cartRepo.findByUserIdAndProductId(userId, productId);

        if (existing == null) return getCart(token);

        if (qty <= 0) {
            cartRepo.delete(existing);
            return getCart(token);
        }

        existing.setQuantity(qty);
        cartRepo.save(existing);
        return getCart(token);
    }

    public List<CartItemResponse> removeFromCart(String token, Integer productId) {
        Integer userId = getUserId(token);

        CartItem existing = cartRepo.findByUserIdAndProductId(userId, productId);
        if (existing != null) cartRepo.delete(existing);

        return getCart(token);
    }
}
