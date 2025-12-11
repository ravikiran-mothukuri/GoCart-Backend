package com.MyAmazon.MyAmazon.service;

import com.MyAmazon.MyAmazon.model.OrderHistory;
import com.MyAmazon.MyAmazon.repository.OrderHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderHistoryService {
    private final OrderHistoryRepository orderHistoryRepository;

    public OrderHistoryService(OrderHistoryRepository orderHistoryRepository){
        this.orderHistoryRepository= orderHistoryRepository;
    }

    public void log(Integer orderId,String status){
        OrderHistory hist= new OrderHistory();
        hist.setOrderId(orderId);
        hist.setStatus(status);
        hist.setUpdatedAt(LocalDateTime.now());
        orderHistoryRepository.save(hist);
    }
}
