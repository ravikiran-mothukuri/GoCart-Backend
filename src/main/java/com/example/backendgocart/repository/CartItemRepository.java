package com.example.backendgocart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backendgocart.model.CartItem;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByUserId(Integer userId);
    CartItem findByUserIdAndProductId(Integer userId, Integer productId);
    void deleteByUserId(Integer userId);
    void deleteByProductId(Integer productId);

}
