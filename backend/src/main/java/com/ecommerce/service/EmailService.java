package com.ecommerce.service;

import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * EmailService — sends order confirmation and cancellation emails.
 *
 * WHY @Autowired(required = false): JavaMailSender bean will fail to initialize
 * if SMTP credentials are wrong or the mail server is unreachable at startup
 * time. required=false means Spring won't crash the whole app if mail is
 * misconfigured — emailService simply won't send, which is acceptable since
 * email is a non-critical feature (orders still work without it). Every send
 * method null-checks mailSender before attempting to send.
 */
@Service
public class EmailService {

    // required=false — app starts even if mail config is missing/wrong
    @Autowired(required = false)
    private JavaMailSender mailSender;

    // Java 8 DateTimeFormatter — reused across both email methods
    private static final DateTimeFormatter FORMATTER
            = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    /**
     * Send order confirmation email after successful checkout. Always wrapped
     * in try-catch — email failure must never break an order.
     */
    public void sendOrderConfirmation(Order order) {
        if (mailSender == null) {
            System.out.println("[WARN] Mail not configured — skipping confirmation email for order #" + order.getId());
            return;
        }
        try {
            String subject = "Order Confirmed! #" + order.getId() + " - ShopEase";

            StringBuilder body = new StringBuilder();
            body.append("Hi ").append(order.getUser().getFirstName()).append(",\n\n");
            body.append("Your order has been placed successfully!\n\n");
            body.append("ORDER DETAILS\n");
            body.append("─────────────────────────────\n");
            body.append("Order ID    : #").append(order.getId()).append("\n");
            body.append("Placed On   : ").append(order.getPlacedAt().format(FORMATTER)).append("\n");
            body.append("Payment     : ").append(order.getPaymentMethod()).append("\n");
            body.append("Delivery    : ").append(order.getDeliveryOption()).append("\n\n");
            body.append("ITEMS ORDERED\n");
            body.append("─────────────────────────────\n");

            // Build itemised list using Java 8 BigDecimal arithmetic
            for (OrderItem item : order.getItems()) {
                body.append("• ").append(item.getProduct().getName())
                        .append(" x").append(item.getQuantity())
                        .append("  ₹").append(
                        item.getPriceAtPurchase()
                                .multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                        .append("\n");
            }

            body.append("─────────────────────────────\n");
            body.append("TOTAL       : ₹").append(order.getTotalAmount()).append("\n\n");
            body.append("Ship To     : ").append(order.getShippingAddress()).append("\n\n");
            body.append("Thank you for shopping with ShopEase!\n");
            body.append("Track your order in your profile.\n\n");
            body.append("— The ShopEase Team");

            sendEmail(order.getUser().getEmail(), subject, body.toString());
            System.out.println("[INFO] Confirmation email sent → " + order.getUser().getEmail());

        } catch (Exception e) {
            // Non-fatal — order is already saved, just log the mail failure
            System.out.println("[WARN] Confirmation email failed: " + e.getMessage());
        }
    }

    /**
     * Send order cancellation email. Always wrapped in try-catch — same
     * resilience pattern as above.
     */
    public void sendCancellationEmail(Order order) {
        if (mailSender == null) {
            System.out.println("[WARN] Mail not configured — skipping cancellation email for order #" + order.getId());
            return;
        }
        try {
            String subject = "Order Cancelled #" + order.getId() + " - ShopEase";

            StringBuilder body = new StringBuilder();
            body.append("Hi ").append(order.getUser().getFirstName()).append(",\n\n");
            body.append("Your order #").append(order.getId()).append(" has been cancelled.\n\n");
            body.append("Reason    : ").append(order.getCancellationReason()).append("\n\n");
            body.append("Refund    : ₹").append(order.getTotalAmount()).append("\n");
            body.append("Timeline  : Refund will be processed within 5-7 business days.\n\n");
            body.append("We hope to see you again!\n");
            body.append("— The ShopEase Team");

            sendEmail(order.getUser().getEmail(), subject, body.toString());
            System.out.println("[INFO] Cancellation email sent → " + order.getUser().getEmail());

        } catch (Exception e) {
            System.out.println("[WARN] Cancellation email failed: " + e.getMessage());
        }
    }

    /**
     * Reusable internal send — single point for actual SMTP dispatch. All email
     * methods call this to keep the sending logic DRY.
     */
    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
