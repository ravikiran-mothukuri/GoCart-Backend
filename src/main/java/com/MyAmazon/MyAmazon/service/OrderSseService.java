package com.MyAmazon.MyAmazon.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


@Service
public class OrderSseService {

//    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
        private final Map<Integer, CopyOnWriteArrayList<SseEmitter>> orderEmitters
        = new ConcurrentHashMap<>();

    public SseEmitter createEmitter(Integer orderId) {
        SseEmitter emitter = new SseEmitter(0L);
        orderEmitters.computeIfAbsent(orderId,k-> new CopyOnWriteArrayList<>())
                        .add(emitter);
        emitter.onCompletion(() -> removeEmitter(orderId, emitter));
        emitter.onTimeout(() -> removeEmitter(orderId, emitter));
        emitter.onError(e -> removeEmitter(orderId, emitter));

        return emitter;
    }

    private void removeEmitter(Integer orderId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> list = orderEmitters.get(orderId);
        if (list != null) {
            list.remove(emitter);
            if (list.isEmpty()) {
                orderEmitters.remove(orderId);
            }
        }
    }

    public void sendUpdate(Integer orderId, String status) {
        CopyOnWriteArrayList<SseEmitter> list = orderEmitters.get(orderId);
        if (list == null) return;

        for (SseEmitter emitter : list) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .name("order-status")
                                .data(status)
                );
            } catch (Exception e) {
                removeEmitter(orderId, emitter);
            }
        }
    }

    public void sendLocationUpdate(Integer orderId, double lat, double lon) {
        CopyOnWriteArrayList<SseEmitter> list = orderEmitters.get(orderId);
        if (list == null) return;

        Map<String, Object> payload = new HashMap<>();
        payload.put("lat", lat);
        payload.put("lon", lon);

        for (SseEmitter emitter : list) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .name("location-update")
                                .data(payload)
                );
            } catch (Exception e) {
                removeEmitter(orderId, emitter);
            }
        }
    }

}
