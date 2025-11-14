package com.MyAmazon.MyAmazon.dto;

import lombok.Data;

@Data
public class CartItemResponse {
    private Integer cartItemId;
    private Integer productId;
    private Integer quantity;

    private String name;
    private String brand;
    private Double price;
    private String category;
    private String imageUrl;
}
