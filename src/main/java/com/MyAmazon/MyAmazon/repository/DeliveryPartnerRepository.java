package com.MyAmazon.MyAmazon.repository;

import com.MyAmazon.MyAmazon.model.DeliveryPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner,Integer> {

    // find the delivery partner who is online= on and status is available.
    @Query("SELECT dp FROM DeliveryPartner dp WHERE dp.status = 'AVAILABLE' AND dp.online = 'ON'")
    List<DeliveryPartner> findAvailablePartners();

    List<DeliveryPartner> findByStatus(String status);
    List<DeliveryPartner> findByOnline(String online);

    Optional<DeliveryPartner> findByUsername(String username);
    Optional<DeliveryPartner> findByCurrentOrderId(Integer orderId);

    @Query("SELECT dp FROM DeliveryPartner dp WHERE dp.status = 'AVAILABLE' AND dp.online = 'ON' " +
            "AND dp.currentLatitude BETWEEN :minLat AND :maxLat " +
            "AND dp.currentLongitude BETWEEN :minLon AND :maxLon")
    List<DeliveryPartner> findPartnersInRange(double minLat, double maxLat,double minLon, double maxLon);

    Optional<Object> findByMobile(String mobile);

    List<DeliveryPartner> findByOnlineAndStatus(String on, String available);
}
