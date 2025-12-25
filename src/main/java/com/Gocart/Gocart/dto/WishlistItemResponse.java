package com.Gocart.Gocart.dto;

import lombok.Data;

@Data
public class WishlistItemResponse {
    private Integer wishlistItemId;
    private Integer productId;

    private String name;
    private String brand;
    private Double price;
    private String category;

    private String imageUrl;
}
