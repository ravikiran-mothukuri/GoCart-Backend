package com.example.backendgocart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backendgocart.model.OrderItem;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem,Integer> {
    List<OrderItem> findByOrderId(Integer orderId);
}
