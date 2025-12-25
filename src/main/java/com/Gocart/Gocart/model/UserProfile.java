package com.Gocart.Gocart.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_profile")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int userId;
    private String firstname;
    private String email;
    private String mobile;
    private String gender;
    private String address;

    private Double currentLatitude;
    private Double currentLongitude;

    private String houseNo;
    private String buildingName;
    private String receiverName;
    private String receiverMobile;

    private String country;
    private String language;

}
