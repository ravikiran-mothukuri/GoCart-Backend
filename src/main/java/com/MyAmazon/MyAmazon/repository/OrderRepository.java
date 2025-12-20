package com.MyAmazon.MyAmazon.repository;

import com.MyAmazon.MyAmazon.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Integer> {



    List<Order> findByDeliveryPartnerId(Integer partnerId);

    List<Order> findOrderByUserId(Integer userId);

    List<Order> findByDeliveryPartnerIdAndStatusNot(Integer deliveryPartnerId, String status);

}
