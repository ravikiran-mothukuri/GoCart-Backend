package com.Gocart.Gocart.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.transaction.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.Gocart.Gocart.dto.CartItemResponse;
import com.Gocart.Gocart.model.CartItem;
import com.Gocart.Gocart.model.Product;
import com.Gocart.Gocart.model.User;
import com.Gocart.Gocart.repository.CartItemRepository;
import com.Gocart.Gocart.repository.ProductRepository;
import com.Gocart.Gocart.repository.UserRepository;
import com.Gocart.Gocart.util.JwtUtil;

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
        if (token == null || token.isBlank())
            return null;

        try {
            String username = jwtUtil.extractUserName(token);
            User user = userRepo.findByUsername(username).orElse(null);
            return user != null ? user.getId() : null;
        } catch (Exception e) {
            System.out.println("Invalid JWT: " + token);
            return null;
        }
    }

    private CartItemResponse toDTO(CartItem item, Product product) {
        CartItemResponse dto = new CartItemResponse();
        dto.setCartItemId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setQuantity(item.getQuantity());

        if (product != null) {
            dto.setName(product.getName());
            dto.setBrand(product.getBrand());
            dto.setPrice(product.getPrice());
            dto.setCategory(product.getCategory());
            dto.setImageUrl(product.getImageUrl()); // FIXED
        }


        return dto;
    }

    public List<CartItemResponse> getCart(String token) {
        Integer userId = getUserId(token);
        List<CartItem> cartItems = cartRepo.findByUserId(userId);

        if (cartItems.isEmpty()) {
            return List.of();
        }

        List<Integer> productIds = cartItems.stream()
                .map(CartItem::getProductId)
                .collect(Collectors.toList());

        Map<Integer, Product> productMap = productRepo.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        return cartItems.stream()
                .map(item -> toDTO(item, productMap.get(item.getProductId())))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<CartItemResponse> addToCart(String token, Integer productId) {
        Integer userId = getUserId(token);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
        }

        Product product= productRepo.findById(productId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND"));

        if(product.getQuantity()<=0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OUT_OF_STOCK");
        }

        CartItem existing = cartRepo.findByUserIdAndProductId(userId, productId);

        if (existing != null) {
            if (existing.getQuantity() + 1 > product.getQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "STOCK_LIMIT_REACHED");
            }
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

    @Transactional
    public List<CartItemResponse> updateQuantity(String token, Integer productId, Integer qty) {
        Integer userId = getUserId(token);

        if(userId==null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
        }

        CartItem existing = cartRepo.findByUserIdAndProductId(userId, productId);

        if (existing == null)
            return getCart(token);

        Product product= productRepo.findById(productId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND"));

        if (qty <= 0) {
            cartRepo.delete(existing);
            return getCart(token);
        }

        if(qty>product.getQuantity()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "STOCK_LIMIT_REACHED");
        }

        existing.setQuantity(qty);
        cartRepo.save(existing);
        return getCart(token);
    }

    @Transactional
    public List<CartItemResponse> removeFromCart(String token, Integer productId) {
        Integer userId = getUserId(token);

        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
        }

        CartItem existing = cartRepo.findByUserIdAndProductId(userId, productId);
        if (existing != null)
            cartRepo.delete(existing);

        return getCart(token);
    }
}
