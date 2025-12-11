package com.MyAmazon.MyAmazon.repository;

import com.MyAmazon.MyAmazon.model.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory,Integer> {

}
