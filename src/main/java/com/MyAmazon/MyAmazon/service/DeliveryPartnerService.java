package com.MyAmazon.MyAmazon.service;

import com.MyAmazon.MyAmazon.model.DeliveryPartner;
import com.MyAmazon.MyAmazon.repository.DeliveryPartnerRepository;
import com.MyAmazon.MyAmazon.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeliveryPartnerService {
    private DeliveryPartnerRepository deliveryPartnerRepository;
    public JwtUtil jwtUtil;
    public PasswordEncoder passwordEncoder;

    public DeliveryPartnerService(DeliveryPartnerRepository deliveryPartnerRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder){
        this.deliveryPartnerRepository= deliveryPartnerRepository;
        this.jwtUtil= jwtUtil;
        this.passwordEncoder= passwordEncoder;
    }

    public DeliveryPartner register(DeliveryPartner deliveryPartner) {
        deliveryPartner.setPassword(passwordEncoder.encode(deliveryPartner.getPassword()));
        if (deliveryPartner.getCurrentLatitude() == null)
            deliveryPartner.setCurrentLatitude(0.0);

        if (deliveryPartner.getCurrentLongitude() == null)
            deliveryPartner.setCurrentLongitude(0.0);

        return deliveryPartnerRepository.save(deliveryPartner);
    }


    public String login(DeliveryPartner deliveryPartner) {
        Optional<DeliveryPartner> partnerLog= deliveryPartnerRepository.findByUsername(deliveryPartner.getUsername());
        if(partnerLog.isPresent()){
            DeliveryPartner part= partnerLog.get();
            if(passwordEncoder.matches(deliveryPartner.getPassword(),part.getPassword())){
                String token= jwtUtil.generateToken(part.getUsername());
                return token;
            }
            else
                throw new RuntimeException("Invalid Password.");

        }
        else
            throw new RuntimeException("User not Found.");
    }

    public DeliveryPartner getByUsername(String username){
        return deliveryPartnerRepository.findByUsername(username).orElseThrow(()-> new RuntimeException("Partner not found."));
    }

    public DeliveryPartner updateLocation(String username, double lat, double lon) {
        DeliveryPartner partner= deliveryPartnerRepository.findByUsername(username).orElseThrow();
        partner.setCurrentLatitude(lat);
        partner.setCurrentLongitude(lon);
        return deliveryPartnerRepository.save(partner);
    }

    public DeliveryPartner updateStatus(String username, String status) {
        DeliveryPartner partner= deliveryPartnerRepository.findByUsername(username).orElseThrow();
        partner.setStatus(status);
        return deliveryPartnerRepository.save(partner);
    }

    public void markOrderPicked(String username, Integer orderId) {
        DeliveryPartner partner= deliveryPartnerRepository.findByUsername(username).orElseThrow();
        partner.setCurrentOrderId(orderId);
        partner.setStatus("BUSY");
        deliveryPartnerRepository.save(partner);
    }

    public void markDelivered(String username, Integer orderId) {
        DeliveryPartner partner= deliveryPartnerRepository.findByUsername(username).orElseThrow();
        partner.setCurrentOrderId(null);
        partner.setStatus("AVAILABLE");
        deliveryPartnerRepository.save(partner);
    }
}
