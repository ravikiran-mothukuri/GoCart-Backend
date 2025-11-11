package com.MyAmazon.MyAmazon.controller;


import com.MyAmazon.MyAmazon.model.User;
import com.MyAmazon.MyAmazon.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
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

    public String login(@RequestBody User login){
        return userservice.login(login);
    }
}
