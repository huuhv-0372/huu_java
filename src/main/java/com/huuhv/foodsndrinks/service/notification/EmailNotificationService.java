package com.huuhv.foodsndrinks.service.notification;

import com.huuhv.foodsndrinks.enums.OrderStatus;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private static final NumberFormat CURRENCY = NumberFormat.getInstance(new Locale("vi", "VN"));

    private final JavaMailSender mailSender;

    @Value("${app.notification.admin-email}")
    private String adminEmail;

    @Value("${app.notification.mail.enabled:true}")
    private boolean enabled;

    @Async("taskExecutor")
    public void sendNewOrderEmail(OrderPlacedEvent event) {
        String subject = "[F&B Store] Đơn hàng mới #" + event.orderId();
        send(subject, OrderMessageFormatter.buildOrderSummary(event), event.orderId());
    }

    public void sendMonthlyStatistics(int year, int month, BigDecimal revenue, long completedCount,
                                       Map<OrderStatus, Long> statusCounts) {
        String subject = "[F&B Store] Thống kê đơn hàng tháng %02d/%d".formatted(month, year);
        StringBuilder body = new StringBuilder();
        body.append("Thống kê đơn hàng tháng %02d/%d\n\n".formatted(month, year));
        body.append("Đơn hàng hoàn thành: ").append(completedCount).append('\n');
        body.append("Doanh thu: ").append(CURRENCY.format(revenue)).append(" VND\n\n");
        body.append("Chi tiết theo trạng thái:\n");
        statusCounts.forEach((status, count) ->
                body.append("  - ").append(status.getLabel()).append(": ").append(count).append('\n'));
        send(subject, body.toString(), null);
    }

    private void send(String subject, String body, Long orderId) {
        if (!enabled || adminEmail == null || adminEmail.isBlank()) {
            log.warn("Admin email notification skipped (disabled or admin-email not configured)");
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setTo(adminEmail);
            helper.setSubject(subject);
            helper.setText(body, false);
            mailSender.send(message);
        } catch (Exception e) {
            if (orderId != null) {
                log.error("Failed to send admin email for order #{}", orderId, e);
            } else {
                log.error("Failed to send admin statistics email", e);
            }
        }
    }
}
