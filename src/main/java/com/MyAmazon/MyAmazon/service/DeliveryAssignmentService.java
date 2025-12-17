package com.MyAmazon.MyAmazon.service;

import com.MyAmazon.MyAmazon.model.DeliveryPartner;
import com.MyAmazon.MyAmazon.model.Warehouse;
import com.MyAmazon.MyAmazon.repository.DeliveryPartnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DeliveryAssignmentService {

    private static final double MAX_DISTANCE_KM = 50.0;
    private static final Logger logger = LoggerFactory.getLogger(DeliveryAssignmentService.class);
    private final DeliveryPartnerRepository deliveryPartnerRepository;

    public DeliveryAssignmentService(DeliveryPartnerRepository deliveryPartnerRepository){
        this.deliveryPartnerRepository= deliveryPartnerRepository;
    }

    public DeliveryPartner assignPartner(Warehouse warehouse){

        List<DeliveryPartner> partners= deliveryPartnerRepository.findAvailablePartners();

        if(partners.isEmpty()){
            logger.warn("No available delivery partners");
            return null;
        }

        DeliveryPartner bestPartner= null;
        double min_dist= Double.MAX_VALUE;

        for(DeliveryPartner partner: partners){
            double dist= calculateDistance(warehouse.getLatitude(),warehouse.getLongitude(),partner.getCurrentLatitude(),partner.getCurrentLongitude());

            if(dist<=MAX_DISTANCE_KM && dist<min_dist) {
                min_dist = dist;
                bestPartner = partner;
            }
        }

        if(bestPartner!=null){
            bestPartner.setStatus("BUSY");
            deliveryPartnerRepository.save(bestPartner);
            logger.info("Assigned partner {} to warehouse at distance {:.2f} km",bestPartner.getUsername(), min_dist);
        }
        else {
            logger.warn("No partners found within {} km of warehouse", MAX_DISTANCE_KM);
        }
        return bestPartner;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double EARTH_RADIUS_KM = 6371.0;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.sin(dLon / 2) * Math.sin(dLon / 2)
                * Math.cos(lat1Rad) * Math.cos(lat2Rad);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

}
