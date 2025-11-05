package com.MyAmazon.MyAmazon.service;

import com.MyAmazon.MyAmazon.model.Product;
import com.MyAmazon.MyAmazon.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repo;

    @Autowired
    public ProductService(ProductRepository repo){
        this.repo= repo;
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



    public void deleteProductById(int id) {
        repo.deleteById(id);
    }
}




