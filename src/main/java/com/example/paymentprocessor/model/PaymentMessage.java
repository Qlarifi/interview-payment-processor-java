package com.example.paymentprocessor.model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Message payload for payment processing. Represents a single unit of work
 * consumed from the queue by a worker.
 */
public class PaymentMessage {

    private final String messageId;
    private final String paymentId;
    private final BigDecimal amount;

    public PaymentMessage(String messageId, String paymentId, BigDecimal amount) {
        this.messageId = messageId;
        this.paymentId = paymentId;
        this.amount = amount;
    }

    public static PaymentMessage of(String paymentId, BigDecimal amount) {
        return new PaymentMessage(UUID.randomUUID().toString(), paymentId, amount);
    }

    public String getMessageId() {
        return messageId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
