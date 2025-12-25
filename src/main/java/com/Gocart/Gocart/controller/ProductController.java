package com.Gocart.Gocart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Gocart.Gocart.model.Product;
import com.Gocart.Gocart.model.User;
import com.Gocart.Gocart.repository.UserRepository;
import com.Gocart.Gocart.service.ProductService;
import com.Gocart.Gocart.util.JwtUtil;

import java.util.List;

@RestController
// @CrossOrigin(origins = "http://localhost:5173")
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
            @RequestBody Product product) {

        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUserName(token);

        User user = userRepo.findByUsername(username).orElse(null);
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Product savedProduct = service.addProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
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

    @GetMapping("/api/products/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String query){
        return ResponseEntity.ok(service.getSearchProducts(query));
    }

}
