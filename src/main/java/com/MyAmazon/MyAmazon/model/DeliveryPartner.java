package com.MyAmazon.MyAmazon.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "delivery_partner")

public class DeliveryPartner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;
    private String mobile;

    private String password;

    private String online= "OFF";
    private String status= "IDLE";

    private Double currentLatitude= 0.0;
    private Double currentLongitude= 0.0;

    private String warehouseId= null;

    private Integer currentOrderId= null;


}
