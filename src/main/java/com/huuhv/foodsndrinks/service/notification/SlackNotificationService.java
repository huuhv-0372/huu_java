package com.huuhv.foodsndrinks.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackNotificationService {

    private final RestClient restClient;

    @Value("${app.notification.slack.webhook-url:}")
    private String webhookUrl;

    @Value("${app.notification.slack.enabled:true}")
    private boolean enabled;

    @Async("taskExecutor")
    public void sendOrderPlaced(OrderPlacedEvent event) {
        if (!enabled || webhookUrl == null || webhookUrl.isBlank()) {
            log.warn("Slack notification skipped for order #{} (disabled or webhook-url not configured)",
                    event.orderId());
            return;
        }
        try {
            restClient.post()
                    .uri(webhookUrl)
                    .body(Map.of("text", OrderMessageFormatter.buildOrderSummary(event)))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Failed to send Slack notification for order #{}", event.orderId(), e);
        }
    }
}
