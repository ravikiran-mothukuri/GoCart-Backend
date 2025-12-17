package com.MyAmazon.MyAmazon.dto;

import lombok.Data;

@Data
public class DeliveryProfileUpdateDTO {
    private String name;
    private String mobile;
    private String vehicle;
    private Double currentLatitude;
    private Double currentLongitude;
}
