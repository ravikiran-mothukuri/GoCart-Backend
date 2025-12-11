package com.MyAmazon.MyAmazon.service;

import com.MyAmazon.MyAmazon.model.DeliveryPartner;
import com.MyAmazon.MyAmazon.model.Warehouse;
import com.MyAmazon.MyAmazon.repository.DeliveryPartnerRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DeliveryAssignmentService {
    private final DeliveryPartnerRepository deliveryPartnerRepository;

    public DeliveryAssignmentService(DeliveryPartnerRepository deliveryPartnerRepository){
        this.deliveryPartnerRepository= deliveryPartnerRepository;
    }

    public DeliveryPartner assignPartner(Warehouse warehouse){
        List<DeliveryPartner> partners= deliveryPartnerRepository.findByStatus("AVAILABLE");
        if(partners.isEmpty()){
            return null;
        }
        DeliveryPartner best= null;
        double min_dist= Integer.MAX_VALUE;

        for(DeliveryPartner partner: partners){
            double dist= distance(warehouse.getLatitude(),warehouse.getLongitude(),partner.getCurrentLatitude(),partner.getCurrentLongitude());

            if(dist<min_dist) {
                min_dist = dist;
                best = partner;
            }
        }
        if(best!=null){
            best.setStatus("BUSY");
            deliveryPartnerRepository.save(best);
        }
        return best;
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat/2)*Math.sin(dLat/2)
                + Math.sin(dLon/2)*Math.sin(dLon/2)
                * Math.cos(lat1) * Math.cos(lat2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }
}
