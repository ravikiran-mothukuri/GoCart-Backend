package com.Gocart.Gocart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Gocart.Gocart.model.WarehouseInventory;

@Repository
public interface WarehouseInventoryRepository extends JpaRepository<WarehouseInventory,Integer> {
    WarehouseInventory findByWarehouseIdAndProductId(Integer warehouseId, Integer productId);

    @Modifying
    @Query("UPDATE WarehouseInventory w SET w.quantity = w.quantity - :qty " +
            "WHERE w.warehouseId = :warehouseId AND w.productId = :productId AND w.quantity >= :qty")
    int decrementStockIfAvailable(@Param("warehouseId") Integer warehouseId,
                                  @Param("productId") Integer productId,
                                  @Param("qty") Integer qty);

}
