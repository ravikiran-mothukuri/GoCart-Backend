package com.MyAmazon.MyAmazon.controller;

import com.MyAmazon.MyAmazon.model.DeliveryPartner;
import com.MyAmazon.MyAmazon.model.Order;
import com.MyAmazon.MyAmazon.service.DeliveryNotificationSseService;
import com.MyAmazon.MyAmazon.service.DeliveryPartnerService;
import com.MyAmazon.MyAmazon.service.OrderService;
import com.MyAmazon.MyAmazon.service.OrderSseService;
import com.MyAmazon.MyAmazon.util.JwtUtil;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
@RequestMapping("/api/order")
public class OrderSseController {
    private final OrderSseService orderSseService;
    private final JwtUtil jwtUtil;
    private final OrderService orderService;
    private final DeliveryPartnerService deliveryPartnerService;
    private final DeliveryNotificationSseService deliveryNotificationSseService;

    public OrderSseController(OrderSseService orderSseService,JwtUtil jwtUtil,OrderService orderService,DeliveryPartnerService deliveryPartnerService,DeliveryNotificationSseService deliveryNotificationSseService){
        this.orderSseService= orderSseService;
        this.jwtUtil= jwtUtil;
        this.orderService= orderService;
        this.deliveryPartnerService= deliveryPartnerService;
        this.deliveryNotificationSseService= deliveryNotificationSseService;
    }

    @GetMapping(value = "/{orderId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamOrder(@PathVariable Integer orderId, @RequestParam String token) {

        String username= jwtUtil.extractUserName(token);
        Order order= orderService.getOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        boolean authorized = false;
        if (order.getUsername().equals(username)) {
            authorized = true;
        }

        try {
            DeliveryPartner partner = deliveryPartnerService.getByUsername(username);
            if (order.getDeliveryPartnerId() != null &&
                    order.getDeliveryPartnerId().equals(partner.getId())) {
                authorized = true;
            }
        } catch (Exception ignored) {}

        if (!authorized) {
            throw new RuntimeException("Unauthorized SSE access");
        }

        return orderSseService.createEmitter(orderId);
    }

    @GetMapping(value = "/notifications", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter deliveryNotifications(@RequestParam String token) {

        String username = jwtUtil.extractUserName(token);
        return deliveryNotificationSseService.createEmitter(username);
    }

//    @GetMapping(value = "/tracking/stream/{orderId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public SseEmitter streamTracking(@PathVariable Integer orderId, @RequestParam String token) {
//        try {
//            // Validate token
//            String username = jwtUtil.extractUserName(token);
//
//            // Verify user owns this order
//            Order order = orderService.getOrderById(orderId);
//            if (order == null || !order.getUsername().equals(username)) {
//                throw new RuntimeException("Unauthorized");
//            }
//
//            return orderSseService.createEmitter(orderId);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to create SSE connection: " + e.getMessage());
//        }
//    }

}
