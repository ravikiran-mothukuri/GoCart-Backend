package com.MyAmazon.MyAmazon.service;

import com.MyAmazon.MyAmazon.dto.DeliveryProfileUpdateDTO;
import com.MyAmazon.MyAmazon.model.DeliveryPartner;
import com.MyAmazon.MyAmazon.model.Order;

import com.MyAmazon.MyAmazon.repository.DeliveryPartnerRepository;
import com.MyAmazon.MyAmazon.repository.OrderRepository;
import com.MyAmazon.MyAmazon.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class DeliveryPartnerService {
    private static final Logger logger = LoggerFactory.getLogger(DeliveryPartnerService.class);

    private DeliveryPartnerRepository deliveryPartnerRepository;
    public JwtUtil jwtUtil;
    public PasswordEncoder passwordEncoder;
    public OrderRepository orderRepository;
    public OrderHistoryService orderHistoryService;

    public DeliveryPartnerService(DeliveryPartnerRepository deliveryPartnerRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder,OrderRepository orderRepository,OrderHistoryService orderHistoryService){
        this.deliveryPartnerRepository= deliveryPartnerRepository;
        this.jwtUtil= jwtUtil;
        this.passwordEncoder= passwordEncoder;
        this.orderRepository= orderRepository;
        this.orderHistoryService= orderHistoryService;
    }

    public DeliveryPartner register(DeliveryPartner deliveryPartner) {

        if (deliveryPartnerRepository.findByUsername(deliveryPartner.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        if (deliveryPartnerRepository.findByMobile(deliveryPartner.getMobile()).isPresent()) {
            throw new RuntimeException("Mobile number already registered");
        }

        deliveryPartner.setPassword(passwordEncoder.encode(deliveryPartner.getPassword()));
        if (deliveryPartner.getCurrentLatitude() == null)
            deliveryPartner.setCurrentLatitude(0.0);

        if (deliveryPartner.getCurrentLongitude() == null)
            deliveryPartner.setCurrentLongitude(0.0);

        // to track and save the important details.
        logger.info("Registering new delivery partner: {}", deliveryPartner.getUsername());

        return deliveryPartnerRepository.save(deliveryPartner);
    }


    public String login(DeliveryPartner deliveryPartner) {

        Optional<DeliveryPartner> partnerLog= deliveryPartnerRepository.findByUsername(deliveryPartner.getUsername());

        if (partnerLog.isEmpty()) {
            logger.warn("Login attempt with non-existent username: {}",
                    deliveryPartner.getUsername());
            throw new RuntimeException("User not found");
        }

        DeliveryPartner partner= partnerLog.get();

        if (!passwordEncoder.matches(deliveryPartner.getPassword(), partner.getPassword())) {
            logger.warn("Invalid password attempt for username: {}",
                    deliveryPartner.getUsername());
            throw new RuntimeException("Invalid password");
        }

        logger.info("Successful login for partner: {}", partner.getUsername());
        return jwtUtil.generateToken(partner.getUsername());
    }

    public DeliveryPartner getByUsername(String username) {
        return deliveryPartnerRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Partner not found: " + username));
    }


    public DeliveryPartner updateLocation(String username, double lat, double lon) {

        if (lat < -90 || lat > 90) {
            throw new IllegalArgumentException("Invalid latitude: must be between -90 and 90");
        }
        if (lon < -180 || lon > 180) {
            throw new IllegalArgumentException("Invalid longitude: must be between -180 and 180");
        }

        DeliveryPartner partner = deliveryPartnerRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Partner not found: " + username));

        partner.setCurrentLatitude(lat);
        partner.setCurrentLongitude(lon);

        logger.info("Updated location for partner {}: ({}, {})", username, lat, lon);
        return deliveryPartnerRepository.save(partner);
    }


    public DeliveryPartner updateOnlineStatus(String username, String status) {

        if (!status.equals("ON") && !status.equals("OFF")) {
            throw new IllegalArgumentException("Invalid online status: must be ON or OFF");
        }

        DeliveryPartner partner = deliveryPartnerRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Partner not found: " + username));

        partner.setOnline(status);

        // Update availability status based on online status
        if (status.equals("ON")) {
            partner.setStatus("AVAILABLE");
        } else {
            partner.setStatus("IDLE");
        }


        logger.info("Updated online status for partner {}: {}", username, status);
        return deliveryPartnerRepository.save(partner);
    }


    public void markOrderPicked(String username, Integer orderId) {
        DeliveryPartner partner = deliveryPartnerRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Partner not found: " + username));

        if (partner.getCurrentOrderId() != null) {
            throw new RuntimeException("Partner already has an active order");
        }

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        partner.setCurrentOrderId(orderId);
        partner.setStatus("BUSY");
        deliveryPartnerRepository.save(partner);

        order.setStatus("PICKED_UP");
        orderRepository.save(order);
//        logger.info("Partner {} picked up order {}", username, orderId);
        orderHistoryService.log(orderId, "PICKED_UP");

    }


    public void markDelivered(String username, Integer orderId) {
        DeliveryPartner partner = deliveryPartnerRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Partner not found: " + username));

        if (partner.getCurrentOrderId() == null || !partner.getCurrentOrderId().equals(orderId)) {
            throw new RuntimeException("Order ID mismatch or no active order");
        }

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        partner.setCurrentOrderId(null);

        // Set status based on online status
        if (partner.getOnline().equals("ON")) {
            partner.setStatus("AVAILABLE");
        } else {
            partner.setStatus("IDLE");
        }

        deliveryPartnerRepository.save(partner);
        order.setStatus("DELIVERED");
        orderRepository.save(order);
        orderHistoryService.log(orderId, "DELIVERED");

    }


    public java.util.Map<String, Object> getPartnerStats(String username) {
        DeliveryPartner partner = getByUsername(username);

        // This would typically query order history
        // For now, returning mock data structure
        return java.util.Map.of(
                "totalDeliveries", 0,
                "todayDeliveries", 0,
                "totalEarnings", 0.0,
                "todayEarnings", 0.0,
                "rating", 5.0
        );
    }


    public DeliveryPartner updateProfile(String username, DeliveryProfileUpdateDTO dto) {

        DeliveryPartner currentData= deliveryPartnerRepository.findByUsername(username).orElseThrow(()-> new RuntimeException("No Deliver User is Found."));

        currentData.setMobile(dto.getMobile());
        currentData.setName(dto.getName());
        currentData.setVehicle(dto.getVehicle());

        if (dto.getCurrentLatitude() != null)
            currentData.setCurrentLatitude(dto.getCurrentLatitude());

        if (dto.getCurrentLongitude() != null)
            currentData.setCurrentLongitude(dto.getCurrentLongitude());

        return deliveryPartnerRepository.save(currentData);

    }
}
