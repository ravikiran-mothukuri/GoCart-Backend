package com.MyAmazon.MyAmazon.service;

import com.MyAmazon.MyAmazon.model.Product;
import com.MyAmazon.MyAmazon.repository.CartItemRepository;
import com.MyAmazon.MyAmazon.repository.ProductRepository;
import com.MyAmazon.MyAmazon.repository.WishlistItemRepository;
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


    @Autowired
    public ProductService(ProductRepository repo, CartItemRepository cartRepo, WishlistItemRepository wishlistRepo){
        this.repo= repo;
        this.cartRepo= cartRepo;
        this.wishlistRepo= wishlistRepo;
    }

    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    public Product getProductById(int id) {
        return repo.findById(id).orElse(null);
    }



    public Product addProduct(Product product, MultipartFile imageFile) throws IOException {
        try {
            product.setImageName(imageFile.getOriginalFilename());
            product.setImageType(imageFile.getContentType());
            product.setImageData(imageFile.getBytes());
        } catch (IOException e){
            throw new IOException("Error processing image file", e);
        }
        return repo.save(product);
    }


    @Transactional
    public void deleteProductById(int id) {
        cartRepo.deleteByProductId(id);

        // 2. Remove product from wishlist
        wishlistRepo.deleteByProductId(id);
        repo.deleteById(id);
    }
}




