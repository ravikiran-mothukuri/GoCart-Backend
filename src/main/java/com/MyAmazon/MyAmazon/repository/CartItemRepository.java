package com.MyAmazon.MyAmazon.repository;

import com.MyAmazon.MyAmazon.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByUserId(Integer userId);
    CartItem findByUserIdAndProductId(Integer userId, Integer productId);
    void deleteByUserId(Integer userId);
    void deleteByProductId(Integer productId);

}
