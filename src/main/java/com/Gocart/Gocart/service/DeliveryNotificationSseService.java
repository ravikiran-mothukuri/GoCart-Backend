package com.Gocart.Gocart.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DeliveryNotificationSseService {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();



    public SseEmitter createEmitter(String username) {
        SseEmitter emitter = new SseEmitter(0L); // no timeout
        emitters.put(username, emitter);

        emitter.onCompletion(() -> emitters.remove(username));
        emitter.onTimeout(() -> emitters.remove(username));
        emitter.onError(e -> emitters.remove(username));

        return emitter;
    }

    public void sendNewOrder(String username, Object orderPayload) {
        SseEmitter emitter = emitters.get(username);
        if (emitter == null) return;

        try {
            emitter.send(
                    SseEmitter.event()
                            .name("new-order")
                            .data(orderPayload)
            );
        } catch (IOException e) {
            emitters.remove(username);
        }
    }
}
