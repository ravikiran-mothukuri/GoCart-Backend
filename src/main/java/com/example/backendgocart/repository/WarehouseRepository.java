package com.example.backendgocart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backendgocart.model.Warehouse;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse,Integer> {
}
