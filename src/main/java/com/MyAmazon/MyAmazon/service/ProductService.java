package com.MyAmazon.MyAmazon.service;

import com.MyAmazon.MyAmazon.model.Product;
import com.MyAmazon.MyAmazon.model.Warehouse;
import com.MyAmazon.MyAmazon.model.WarehouseInventory;
import com.MyAmazon.MyAmazon.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    public ProductService(ProductRepository repo, CartItemRepository cartRepo, WishlistItemRepository wishlistRepo,WarehouseRepository warehouseRepo,WarehouseInventoryRepository inventoryRepo){
        this.repo= repo;
        this.cartRepo= cartRepo;
        this.wishlistRepo= wishlistRepo;
        this.warehouseRepo= warehouseRepo;
        this.inventoryRepo= inventoryRepo;
    }

    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    public Product getProductById(int id) {
        return repo.findById(id).orElse(null);
    }


    @Transactional
    public Product addProduct(Product product) {
        Product savedProduct= repo.save(product);
        Warehouse defaultWarehouse = warehouseRepo.findById(1).orElseThrow(() -> new RuntimeException("Default warehouse not found"));

        WarehouseInventory inventory = new WarehouseInventory();
        inventory.setProductId(product.getId());
        inventory.setWarehouseId(defaultWarehouse.getId());
        inventory.setQuantity(100);
        inventoryRepo.save(inventory);

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




