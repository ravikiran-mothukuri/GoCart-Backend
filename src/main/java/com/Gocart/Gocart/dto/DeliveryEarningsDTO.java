package com.Gocart.Gocart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryEarningsDTO {
    private Double todayEarnings;
    private Double weekEarnings;
    private Double totalEarnings;
    private Integer todayDeliveries;
    private Integer weekDeliveries;
    private Integer totalDeliveries;
    private Double averageOrderValue;
}
