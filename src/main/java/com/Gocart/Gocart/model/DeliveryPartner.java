package com.Gocart.Gocart.model;


import jakarta.persistence.*;
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
    private String name= "User";

    @Column(nullable = false)
    private String online = "OFF";

    @Column(nullable = false)
    private String status = "IDLE";


    private String vehicle= "BIKE"; // BIKE, SCOOTER.

    private Double currentLatitude= 0.0;
    private Double currentLongitude= 0.0;

    private String warehouseId= null;

    private Integer currentOrderId= null;


}
