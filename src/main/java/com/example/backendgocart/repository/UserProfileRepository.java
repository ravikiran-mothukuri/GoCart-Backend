package com.example.backendgocart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backendgocart.model.UserProfile;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
    Optional<UserProfile> findByUserId(int userId);
}
