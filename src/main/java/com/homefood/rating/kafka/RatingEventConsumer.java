package com.homefood.rating.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RatingEventConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order.delivered", groupId = "rating-service")
    public void handleOrderDelivered(@Payload String message) {
        try {
            JsonNode node = objectMapper.readTree(message);
            String orderId = node.get("orderId").asText();
            String buyerId = node.get("buyerId").asText();
            log.info("Order delivered - prompting rating for orderId: {} buyerId: {}", orderId, buyerId);
            // Rating prompt is sent via notification service; rating submission happens via API
        } catch (Exception e) {
            log.error("Error handling order.delivered in rating service: {}", e.getMessage());
        }
    }
}
