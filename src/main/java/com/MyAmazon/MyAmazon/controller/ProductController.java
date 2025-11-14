package com.MyAmazon.MyAmazon.controller;

import com.MyAmazon.MyAmazon.model.Product;
import com.MyAmazon.MyAmazon.model.User;
import com.MyAmazon.MyAmazon.repository.UserRepository;
import com.MyAmazon.MyAmazon.service.ProductService;
import com.MyAmazon.MyAmazon.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    private final ProductService service;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    public ProductController(ProductService service) {
        this.service = service;
    }


    //   PUBLIC: Get all products

    @GetMapping("/api/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return new ResponseEntity<>(service.getAllProducts(), HttpStatus.OK);
    }


    //   PUBLIC: Get product by ID

    @GetMapping("/api/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id) {
        Product product = service.getProductById(id);
        if (product != null) {
            return new ResponseEntity<>(product, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    //   ADMIN ONLY: Add product

    @PostMapping("/api/addProduct")
    public ResponseEntity<Product> addProduct(
            @RequestHeader("Authorization") String authHeader,
            @RequestPart Product product,
            @RequestPart MultipartFile imageFile) {

        // Extract token
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUserName(token);

        // Fetch user
        User user = userRepo.findByUsername(username).orElse(null);
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            Product savedProduct = service.addProduct(product, imageFile);
            return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //   PUBLIC: Get image by product ID

    @GetMapping("/api/products/{id}/image")
    public ResponseEntity<byte[]> getImageByProductId(@PathVariable int id) {
        Product product = service.getProductById(id);
        if (product != null && product.getImageData() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(product.getImageType()))
                    .body(product.getImageData());
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    //   ADMIN ONLY: Delete product

    @DeleteMapping("/api/product/{id}")
    public ResponseEntity<Void> deleteProductById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int id) {

        // Extract token
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUserName(token);

        // Fetch user
        User user = userRepo.findByUsername(username).orElse(null);
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        service.deleteProductById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
