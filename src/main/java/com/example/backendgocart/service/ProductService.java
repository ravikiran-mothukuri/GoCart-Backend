package com.example.backendgocart.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.backendgocart.model.Product;
import com.example.backendgocart.model.Warehouse;
import com.example.backendgocart.model.WarehouseInventory;
import com.example.backendgocart.repository.*;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repo;
    private CartItemRepository cartRepo;
    private WishlistItemRepository wishlistRepo;

    private final WarehouseRepository warehouseRepo;
    private final WarehouseInventoryRepository inventoryRepo;

    @Autowired
    public ProductService(ProductRepository repo, CartItemRepository cartRepo, WishlistItemRepository wishlistRepo,
            WarehouseRepository warehouseRepo, WarehouseInventoryRepository inventoryRepo) {
        this.repo = repo;
        this.cartRepo = cartRepo;
        this.wishlistRepo = wishlistRepo;
        this.warehouseRepo = warehouseRepo;
        this.inventoryRepo = inventoryRepo;
    }

    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    public Product getProductById(int id) {
        return repo.findById(id).orElse(null);
    }

    @Transactional
    public Product addProduct(Product product) {
        Product savedProduct = repo.save(product);

        // Add inventory to ALL warehouses
        List<Warehouse> allWarehouses = warehouseRepo.findAll();

        if (allWarehouses.isEmpty()) {
            throw new RuntimeException("No warehouses found. Please create at least one warehouse.");
        }

        for (Warehouse warehouse : allWarehouses) {
            WarehouseInventory inventory = new WarehouseInventory();
            inventory.setProductId(savedProduct.getId());
            inventory.setWarehouseId(warehouse.getId());
            inventory.setQuantity(100); // Default: 100 units per warehouse
            inventoryRepo.save(inventory);
        }

        return savedProduct;
    }

    @Transactional
    public void deleteProductById(int id) {
        cartRepo.deleteByProductId(id);

        // 2. Remove product from wishlist
        wishlistRepo.deleteByProductId(id);
        repo.deleteById(id);
    }

    public List<Product> getSearchProducts(String query) {

        return repo.searchByNameOrCategoryOrDescription(query);
    }
}
