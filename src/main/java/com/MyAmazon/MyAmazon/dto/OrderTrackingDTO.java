package com.MyAmazon.MyAmazon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderTrackingDTO {
    private Integer orderId;
    private String customerName;
    private String customerAddress;
    private String customerMobile;
    private Double customerLatitude;
    private Double customerLongitude;
    private Double deliveryPartnerLatitude;
    private Double deliveryPartnerLongitude;

    private String deliveryPersonName;
    private String deliveryMobile;
    private String status;
    private Double totalPrice;
    private Integer itemCount;
}
