package com.MyAmazon.MyAmazon.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String brand;
    private String description;
    private Double price;
    private String category;
    private Integer quantity;
    private LocalDate releasedate;
    private Boolean available;

    private String imageUrl;


}
