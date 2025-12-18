package com.MyAmazon.MyAmazon.repository;

import com.MyAmazon.MyAmazon.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Integer> {



    @Query("SELECT ord FROM Order ord WHERE ord.deliveryPartnerId = :id AND ord.status <> :status")
    List<Order> findByDeliveryPartnerIdAndStatusNot(@Param("id") Integer deliveryPartnerId, @Param("status") String status);

    List<Order> findByDeliveryPartnerId(Integer partnerId);

    List<Order> findOrderByUserId(Integer userId);
}
