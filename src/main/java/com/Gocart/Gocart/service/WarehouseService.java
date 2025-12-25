package com.Gocart.Gocart.service;

import org.springframework.stereotype.Service;

import com.Gocart.Gocart.model.Warehouse;
import com.Gocart.Gocart.repository.WarehouseRepository;

import java.util.List;

@Service
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;

    public WarehouseService(WarehouseRepository warehouseRepository){
        this.warehouseRepository= warehouseRepository;
    }

    public List<Warehouse> getAllWarehouses(){
        return warehouseRepository.findAll();
    }

    public Warehouse getWarehouseById(Integer id){
        return warehouseRepository.findById(id).orElse(null);
    }

    public Warehouse findNearestWarehouse(double latitude,double longitude){
        List<Warehouse> warehouses= warehouseRepository.findAll();

        Warehouse nearest= null;
        double min_distance= Integer.MAX_VALUE;

        for(Warehouse warehouse: warehouses){
            double dist= distance(latitude,longitude,warehouse.getLatitude(),warehouse.getLongitude());
            if(min_distance> dist){
                min_distance= dist;
                nearest= warehouse;
            }

        }

        return nearest;
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // earth km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat/2)*Math.sin(dLat/2) +
                Math.sin(dLon/2)*Math.sin(dLon/2)*Math.cos(lat1)*Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c;
    }

}
