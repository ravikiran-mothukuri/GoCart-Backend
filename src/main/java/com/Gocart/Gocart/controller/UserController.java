package com.Gocart.Gocart.controller;


import org.springframework.web.bind.annotation.*;

import com.Gocart.Gocart.model.User;
import com.Gocart.Gocart.service.UserService;

import java.util.Map;

@RestController
// @CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    UserService userservice;
    public UserController(UserService userservice){
        this.userservice= userservice;
    }


    @PostMapping("/api/register")
    public void register(@RequestBody User user){
        userservice.registerUser(user);
    }

    @PostMapping("/api/login")

    public Map<String, String> login(@RequestBody User login){
        return userservice.login(login);
    }
}
