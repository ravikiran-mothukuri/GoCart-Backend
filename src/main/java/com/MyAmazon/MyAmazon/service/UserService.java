package com.MyAmazon.MyAmazon.service;

import com.MyAmazon.MyAmazon.model.User;
import com.MyAmazon.MyAmazon.repository.UserRepository;
import com.MyAmazon.MyAmazon.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service

public class UserService {

    public final UserRepository repo;
    public final JwtUtil jwtUtil;
    public final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repo, JwtUtil jwtUtil, PasswordEncoder passwordEncoder){
        this.repo= repo;
        this.jwtUtil= jwtUtil;
        this.passwordEncoder= passwordEncoder;
    }

    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        repo.save(user);
    }


    public Map<String, String> login(User login) {
        Optional<User> userLog= repo.findByUsername(login.getUsername());
        Map<String,String> response= new HashMap<>();
        if(userLog.isPresent()){
            User usr= userLog.get();
            if(passwordEncoder.matches(login.getPassword(), usr.getPassword())){

                String token= jwtUtil.generateToken(usr.getUsername());
                response.put("token",token);
                response.put("role",usr.getRole());
                return response;
            }
            else
                throw new RuntimeException("Invalid Password.");

        }
        else
            throw new RuntimeException("User not Found.");

    }
}
