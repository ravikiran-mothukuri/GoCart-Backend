package com.example.backendgocart.service;

import org.springframework.stereotype.Service;

import com.example.backendgocart.model.User;
import com.example.backendgocart.model.UserProfile;
import com.example.backendgocart.repository.UserProfileRepository;
import com.example.backendgocart.repository.UserRepository;
import com.example.backendgocart.util.JwtUtil;


@Service
public class ProfileService {
    private UserProfileRepository profileRepo;
    private UserRepository userRepo;
    private JwtUtil jwtUtil;

    public ProfileService(UserProfileRepository profileRepo, UserRepository userRepo, JwtUtil jwtUtil){
        this.profileRepo= profileRepo;
        this.userRepo= userRepo;
        this.jwtUtil= jwtUtil;
    }

    public UserProfile getProfile(String token){
        String username= jwtUtil.extractUserName(token);
        User user= userRepo.findByUsername(username).orElseThrow();
        int userId= user.getId();

        return profileRepo.findByUserId(userId).orElse(null);
    }

    public UserProfile updateProfile(String token, UserProfile updatedData){
        String username = jwtUtil.extractUserName(token);
        User user = userRepo.findByUsername(username).orElseThrow();
        int userId = user.getId();

        UserProfile profile = profileRepo.findByUserId(userId).orElse(new UserProfile());
        profile.setUserId(userId);

        profile.setFirstname(updatedData.getFirstname());
        profile.setEmail(updatedData.getEmail());
        profile.setMobile(updatedData.getMobile());
        profile.setGender(updatedData.getGender());
        profile.setAddress(updatedData.getAddress());
        profile.setCountry(updatedData.getCountry());
        profile.setLanguage(updatedData.getLanguage());

        profile.setHouseNo(updatedData.getHouseNo());
        profile.setBuildingName(updatedData.getBuildingName());
        profile.setReceiverName(updatedData.getReceiverName());
        profile.setReceiverMobile(updatedData.getReceiverMobile());

        profile.setCurrentLatitude(updatedData.getCurrentLatitude());
        profile.setCurrentLongitude(updatedData.getCurrentLongitude());

        return profileRepo.save(profile);
    }

}
