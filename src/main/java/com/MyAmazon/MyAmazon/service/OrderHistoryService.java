package com.MyAmazon.MyAmazon.service;

import com.MyAmazon.MyAmazon.dto.DeliveredOrderDTO;
import com.MyAmazon.MyAmazon.dto.DeliveryEarningsDTO;
import com.MyAmazon.MyAmazon.model.OrderHistory;
import com.MyAmazon.MyAmazon.repository.OrderHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class OrderHistoryService {
    private final OrderHistoryRepository orderHistoryRepository;

    public OrderHistoryService(OrderHistoryRepository orderHistoryRepository){
        this.orderHistoryRepository = orderHistoryRepository;
    }

    public void log(Integer orderId, String status){
        OrderHistory hist = new OrderHistory();
        hist.setOrderId(orderId);
        hist.setStatus(status);
        hist.setUpdatedAt(LocalDateTime.now());
        orderHistoryRepository.save(hist);
    }

    public void log(Integer orderId, String status, Double totalPrice, Integer deliveryPartnerId){
        OrderHistory hist = new OrderHistory();
        hist.setOrderId(orderId);
        hist.setStatus(status);
        hist.setUpdatedAt(LocalDateTime.now());
        hist.setTotalPrice(totalPrice);
        hist.setDeliveryPartnerId(deliveryPartnerId);
        orderHistoryRepository.save(hist);
    }

    public List<OrderHistory> getOrderHistoryByPartnerId(Integer deliveryPartnerId, String status) {
        return orderHistoryRepository.findByDeliveryPartnerIdAndStatus(deliveryPartnerId, status);
    }

    public List<DeliveredOrderDTO> getDeliveredOrders(Integer partnerId) {
        return orderHistoryRepository.findDeliveredOrdersWithCustomerName(partnerId);
    }

    public DeliveryEarningsDTO calculateEarnings(Integer partnerId) {
        LocalDateTime now = LocalDateTime.now();

        // Start of today (00:00:00)
        LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();

        // Start of week (Monday 00:00:00)
        LocalDateTime startOfWeek = now.toLocalDate()
                .minusDays(now.getDayOfWeek().getValue() - 1)
                .atStartOfDay();

        // Calculate today's earnings and deliveries
        Double todayEarnings = orderHistoryRepository
                .sumEarningsByPartnerAndDate(partnerId, startOfToday);
        Integer todayDeliveries = orderHistoryRepository
                .countDeliveriesByPartnerAndDate(partnerId, startOfToday);

        // Calculate this week's earnings and deliveries
        Double weekEarnings = orderHistoryRepository
                .sumEarningsByPartnerAndDate(partnerId, startOfWeek);
        Integer weekDeliveries = orderHistoryRepository
                .countDeliveriesByPartnerAndDate(partnerId, startOfWeek);

        // Calculate total earnings and deliveries
        Double totalEarnings = orderHistoryRepository
                .sumTotalEarningsByPartner(partnerId);
        Integer totalDeliveries = orderHistoryRepository
                .countTotalDeliveriesByPartner(partnerId);

        // Calculate average order value
        Double averageOrderValue = totalDeliveries > 0
                ? totalEarnings / totalDeliveries
                : 0.0;

        return new DeliveryEarningsDTO(
                todayEarnings,
                weekEarnings,
                totalEarnings,
                todayDeliveries,
                weekDeliveries,
                totalDeliveries,
                averageOrderValue
        );
    }
}