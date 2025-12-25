package com.Gocart.Gocart.controller;



import org.springframework.web.bind.annotation.*;

import com.Gocart.Gocart.model.UserProfile;
import com.Gocart.Gocart.service.ProfileService;

@RestController
// @CrossOrigin(origins = "http://localhost:5173")
public class UserProfileController {
    private ProfileService profileService;

    public UserProfileController(ProfileService profileService){
        this.profileService= profileService;
    }

    @GetMapping("/api/user/profile")
    public UserProfile getProfile(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return profileService.getProfile(token);
    }

    @PutMapping("/api/user/profile")
    public UserProfile updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserProfile updated) {

        String token = authHeader.replace("Bearer ", "");
        return profileService.updateProfile(token, updated);
    }

}
