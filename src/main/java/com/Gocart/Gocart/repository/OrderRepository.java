package com.Gocart.Gocart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.Gocart.Gocart.model.Order;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Integer> {



    List<Order> findByDeliveryPartnerId(Integer partnerId);

    List<Order> findOrderByUserId(Integer userId);

    List<Order> findByDeliveryPartnerIdAndStatusNot(Integer deliveryPartnerId, String status);

}
