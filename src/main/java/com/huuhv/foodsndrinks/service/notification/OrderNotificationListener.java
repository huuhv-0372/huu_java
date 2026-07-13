package com.huuhv.foodsndrinks.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderNotificationListener {

    private final SlackNotificationService slackNotificationService;
    private final EmailNotificationService emailNotificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderPlaced(OrderPlacedEvent event) {
        slackNotificationService.sendOrderPlaced(event);
        emailNotificationService.sendNewOrderEmail(event);
    }
}
