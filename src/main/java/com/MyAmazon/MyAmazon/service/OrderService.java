package com.MyAmazon.MyAmazon.service;

import com.MyAmazon.MyAmazon.model.*;
import com.MyAmazon.MyAmazon.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {
    private final OrderRepository orderRepo;
    private final OrderItemRepository itemRepo;
    private final WarehouseService warehouseService;
    private final DeliveryAssignmentService deliveryService;
    private final OrderHistoryService historyService;
    private final CartItemRepository cartRepo;
    private final WarehouseInventoryRepository warehouseInventoryRepository;
    private final UserProfileRepository userProfileRepository;
    private final DeliveryPartnerRepository deliveryPartnerRepository;
    private final ProductRepository productRepository;

    public OrderService(
            OrderRepository orderRepo,
            OrderItemRepository itemRepo,
            WarehouseService warehouseService,
            DeliveryAssignmentService deliveryService,
            OrderHistoryService historyService,
            CartItemRepository cartRepo,
            UserProfileRepository userProfileRepository,
            WarehouseInventoryRepository warehouseInventoryRepository,
            DeliveryPartnerRepository deliveryPartnerRepository,
            ProductRepository productRepository
    ) {
        this.orderRepo = orderRepo;
        this.itemRepo = itemRepo;
        this.warehouseService = warehouseService;
        this.deliveryService = deliveryService;
        this.historyService = historyService;
        this.cartRepo = cartRepo;
        this.userProfileRepository= userProfileRepository;
        this.warehouseInventoryRepository= warehouseInventoryRepository;
        this.deliveryPartnerRepository= deliveryPartnerRepository;
        this.productRepository= productRepository;
    }

    @Transactional
    public Order placeOrder(User user){

        // 1. fetch cart
        List<CartItem> cart= cartRepo.findByUserId(user.getId());
        if(cart.isEmpty())
            throw new RuntimeException("Cart is empty.");

        // 2. fetch user profile
        UserProfile userProfile= userProfileRepository.findByUserId(user.getId()).orElseThrow(()-> new RuntimeException("Profile not found"));

        if (userProfile.getCurrentLatitude() == null || userProfile.getCurrentLongitude() == null)
            throw new RuntimeException("Location must be set before placing order.");


        double userLat= userProfile.getCurrentLatitude();
        double userLon= userProfile.getCurrentLongitude();


        // 3. find nearest warehouse.
        Warehouse nearest= warehouseService.findNearestWarehouse(userLat,userLon);

        if(nearest==null){
            throw new RuntimeException("The Items is not Delivered to your location.");
        }

        Integer warehouseId= nearest.getId();
        // 4. validate the stock in the inventory before proceeding.

        Map<Integer, Product> productMap = new HashMap<>();

        Double totalPrice= 0.0;
        for(CartItem c: cart){

            int productId= c.getProductId();
            Product product = productRepository.findById(c.getProductId())
                    .orElseThrow(() ->
                        new RuntimeException(
                            "The product (ID: " + c.getProductId() +
                            ") is no longer available. Please remove it from the cart."
                        )
                    );

            productMap.put(c.getProductId(), product);

            int qty= c.getQuantity();
            Double unitPrice= product.getPrice();

            totalPrice+= unitPrice*qty;

            WarehouseInventory inventory= warehouseInventoryRepository.findByWarehouseIdAndProductId(warehouseId,productId);
            if(inventory==null){
                throw new RuntimeException("Product " + productId + " unavailable.");
            }
            if(inventory.getQuantity()<qty){
                throw new RuntimeException("Insufficient stock for product " + productId);
            }

            int updated = warehouseInventoryRepository.decrementStockIfAvailable(warehouseId, productId, qty);
            if (updated == 0)
                throw new RuntimeException("Stock unavailable for product " + productId);
        }

        // 5. assigns delivery partner.

        DeliveryPartner partner= deliveryService.assignPartner(nearest);
        if(partner==null)
            throw new RuntimeException("No delivery partner is available.");

        // 5. create order.
        Order order= new Order();
        order.setUserId(user.getId());
        order.setUsername(user.getUsername());
        order.setWarehouseId(nearest.getId());
        order.setDeliveryPartnerId(partner.getId());
        order.setStatus("PLACED");
        order.setPrice(totalPrice); // Update the total Price of the order T.P
        order= orderRepo.save(order);

        partner.setCurrentOrderId(order.getId());
        partner.setStatus("BUSY");
        deliveryPartnerRepository.save(partner);

        // 6. save orderItems.
        for(CartItem c: cart){
            Product product= productMap.get(c.getProductId());

            OrderItem item= new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(c.getProductId());
            item.setQuantity(c.getQuantity());


            item.setPrice(product.getPrice());
            item.setPriceByQuantity(product.getPrice()*c.getQuantity());
            itemRepo.save(item);
        }

        // 7. clear cart.
        cartRepo.deleteByUserId(user.getId());

        // 8. log the history.
        historyService.log(order.getId(), "PLACED",totalPrice,partner.getId());
        return order;
    }

    public void deliveredOrder(String username, Integer orderId) {
        try{
            System.out.println(username+orderId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Order> getOrdersByDeliveryPartnerId(Integer id) {
        return orderRepo.findByDeliveryPartnerIdAndStatusNot(id,"DELIVERED");
    }

    public List<Order> getOrdersByUserId(int id) {
        return orderRepo.findOrderByUserId(id);
    }

    public Order getOrderById(Integer orderId) {
        return orderRepo.findById(orderId)
                .orElse(null);
    }
}
