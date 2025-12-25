package com.Gocart.Gocart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Gocart.Gocart.dto.DeliveredOrderDTO;
import com.Gocart.Gocart.model.OrderHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory,Integer> {

    @Query("""
    SELECT new com.Gocart.Gocart.dto.DeliveredOrderDTO(
    oh.orderId,
    u.firstname,
    oh.totalPrice,
    oh.status,
    oh.updatedAt
)
FROM OrderHistory oh
JOIN Order o ON o.id = oh.orderId
JOIN UserProfile u ON u.userId = o.userId
WHERE oh.deliveryPartnerId = :partnerId
AND oh.status = 'DELIVERED'
""")
    List<DeliveredOrderDTO> findDeliveredOrdersWithCustomerName(@Param("partnerId") Integer partnerId);

    List<OrderHistory> findByDeliveryPartnerIdAndStatus(Integer deliveryPartnerId, String status);


    // New queries for earnings calculation
    @Query("SELECT COALESCE(SUM(oh.totalPrice), 0.0) FROM OrderHistory oh " +
            "WHERE oh.deliveryPartnerId = :partnerId " +
            "AND oh.status = 'DELIVERED' " +
            "AND oh.updatedAt >= :startDate")
    Double sumEarningsByPartnerAndDate(
            @Param("partnerId") Integer partnerId,
            @Param("startDate") LocalDateTime startDate
    );

    @Query("SELECT COUNT(oh) FROM OrderHistory oh " +
            "WHERE oh.deliveryPartnerId = :partnerId " +
            "AND oh.status = 'DELIVERED' " +
            "AND oh.updatedAt >= :startDate")
    Integer countDeliveriesByPartnerAndDate(
            @Param("partnerId") Integer partnerId,
            @Param("startDate") LocalDateTime startDate
    );

    @Query("SELECT COALESCE(SUM(oh.totalPrice), 0.0) FROM OrderHistory oh " +
            "WHERE oh.deliveryPartnerId = :partnerId " +
            "AND oh.status = 'DELIVERED'")
    Double sumTotalEarningsByPartner(@Param("partnerId") Integer partnerId);

    @Query("SELECT COUNT(oh) FROM OrderHistory oh " +
            "WHERE oh.deliveryPartnerId = :partnerId " +
            "AND oh.status = 'DELIVERED'")
    Integer countTotalDeliveriesByPartner(@Param("partnerId") Integer partnerId);

    Optional<OrderHistory> findTopByOrderIdOrderByUpdatedAtDesc(Integer orderId);

}