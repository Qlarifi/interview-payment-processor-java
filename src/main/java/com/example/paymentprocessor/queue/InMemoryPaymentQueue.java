package com.example.paymentprocessor.queue;

import com.example.paymentprocessor.model.PaymentMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * In-memory implementation of the payment message queue.
 * Messages are held in a single pending queue. receive() returns the next
 * message and the implementation is expected to hide it from other consumers
 * until visibility timeout or ack.
 */
@Component
public class InMemoryPaymentQueue implements PaymentMessageQueue {

    private final ConcurrentLinkedQueue<PaymentMessage> pending = new ConcurrentLinkedQueue<>();

    @Value("${app.queue.visibility-timeout-seconds:30}")
    private int visibilityTimeoutSeconds;

    @Override
    public void submit(PaymentMessage message) {
        pending.add(message);
    }

    @Override
    public PaymentMessage receive() {
        if (pending.isEmpty()) {
            return null;
        }
        return pending.peek();
    }

    @Override
    public void ack(String messageId) {
        pending.removeIf(m -> m.getMessageId().equals(messageId));
    }
}
