package com.Gocart.Gocart.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeliveredOrderDTO {
    private Integer orderId;
    private String customerName;
    private Double totalPrice;
    private String status;
    private LocalDateTime deliveredAt;

    public DeliveredOrderDTO(
            Integer orderId,
            String customerName,
            Double totalPrice,
            String status,
            LocalDateTime deliveredAt
    ) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.totalPrice = totalPrice;
        this.status = status;
        this.deliveredAt = deliveredAt;
    }
}
