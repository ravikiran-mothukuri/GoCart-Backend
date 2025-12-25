package com.Gocart.Gocart.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Entity
@Data
@Table(name = "warehouse_inventory",uniqueConstraints = {@UniqueConstraint(columnNames = {"warehouseId", "productId"})})
public class WarehouseInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer warehouseId;

    private Integer productId;

    private Integer quantity;
}
