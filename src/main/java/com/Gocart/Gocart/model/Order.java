package com.Gocart.Gocart.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;
    private Integer userId;
    private Integer warehouseId;

    private Integer deliveryPartnerId;

    private Double price= 0.0;

    private String status;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
       this.createdAt = LocalDateTime.now();
    }
}
