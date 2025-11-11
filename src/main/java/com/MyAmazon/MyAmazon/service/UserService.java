package com.MyAmazon.MyAmazon.service;

import com.MyAmazon.MyAmazon.model.User;
import com.MyAmazon.MyAmazon.repository.UserRepository;
import com.MyAmazon.MyAmazon.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

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
        repo.save(user);
    }


    public String login(User login) {
        Optional<User> userLog= repo.findByUsername(login.getUsername());
        if(userLog.isPresent()){
            User usr= userLog.get();
            if(passwordEncoder.matches(login.getPassword(), usr.getPassword()))
                return jwtUtil.generateToken(usr.getUsername());
            else
                throw new RuntimeException("Invalid Password.");

        }
        else
            throw new RuntimeException("User not Found.");

    }
}
