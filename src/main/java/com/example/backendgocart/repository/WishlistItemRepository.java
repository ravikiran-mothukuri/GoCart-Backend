package com.example.backendgocart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backendgocart.model.WishlistItem;

import java.util.List;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Integer> {

    List<WishlistItem> findByUserId(Integer userId);

    WishlistItem findByUserIdAndProductId(Integer userId, Integer productId);

    void deleteByUserIdAndProductId(Integer userId, Integer productId);

    void deleteByProductId(Integer productId);

}
