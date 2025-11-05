package com.MyAmazon.MyAmazon.repository;

import com.MyAmazon.MyAmazon.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
}
