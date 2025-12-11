package com.MyAmazon.MyAmazon.repository;

import com.MyAmazon.MyAmazon.model.DeliveryPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner,Integer> {
    List<DeliveryPartner> findByStatus(String status);
    Optional<DeliveryPartner> findByUsername(String username);
}
