package com.MyAmazon.MyAmazon.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class DeliveryPartner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;
    private String mobile;

    private String password;

    private String status= "AVAILABLE"; // OFFLINE, ONLINE, AVAILABLE, BUSY

    private Double currentLatitude= 0.0;
    private Double currentLongitude= 0.0;

    private String warehouseId= null;

    private Integer currentOrderId= null;


}
