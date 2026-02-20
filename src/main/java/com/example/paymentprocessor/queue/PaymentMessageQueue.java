package com.example.paymentprocessor.queue;

import com.example.paymentprocessor.model.PaymentMessage;

/**
 * Abstraction for the payment message queue. Supports submit, receive with
 * visibility semantics, and ack. Implementations may be in-memory or external.
 */
public interface PaymentMessageQueue {

    /**
     * Submit a message for processing.
     */
    void submit(PaymentMessage message);

    /**
     * Receive the next available message. The message should not be visible to
     * other consumers until the visibility timeout expires or it is acked.
     *
     * @return the next message, or null if none available
     */
    PaymentMessage receive();

    /**
     * Acknowledge successful processing. The message should be removed so it
     * is not delivered again.
     *
     * @param messageId the message id returned by the received message
     */
    void ack(String messageId);
}
