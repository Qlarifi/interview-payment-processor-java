package com.example.paymentprocessor.consumer;

import com.example.paymentprocessor.model.PaymentMessage;
import com.example.paymentprocessor.queue.PaymentMessageQueue;
import com.example.paymentprocessor.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Starts multiple worker threads that poll the payment queue and process
 * messages. Each worker logs with a distinct worker id.
 */
@Component
public class PaymentQueueConsumer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(PaymentQueueConsumer.class);
    private static final AtomicInteger workerIdCounter = new AtomicInteger(1);

    private final PaymentMessageQueue queue;
    private final PaymentService paymentService;
    private final int pollIntervalMs;
    private final int threadCount;

    public PaymentQueueConsumer(
            PaymentMessageQueue queue,
            PaymentService paymentService,
            @Value("${app.worker.poll-interval-ms:500}") int pollIntervalMs,
            @Value("${app.worker.thread-count:4}") int threadCount
    ) {
        this.queue = queue;
        this.paymentService = paymentService;
        this.pollIntervalMs = pollIntervalMs;
        this.threadCount = threadCount;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (int i = 0; i < threadCount; i++) {
            int workerId = workerIdCounter.getAndIncrement();
            Thread t = new Thread(() -> pollLoop(workerId), "payment-worker-" + workerId);
            t.setDaemon(true);
            t.start();
        }
    }

    private void pollLoop(int workerId) {
        while (true) {
            try {
                PaymentMessage message = queue.receive();
                if (message == null) {
                    Thread.sleep(pollIntervalMs);
                    continue;
                }
                log.info("Worker {} processing payment {}", workerId, message.getPaymentId());
                try {
                    paymentService.processPayment(message);
                    queue.ack(message.getMessageId());
                } catch (Exception e) {
                    log.warn("Worker {} failed to process payment {}: {}", workerId, message.getPaymentId(), e.getMessage());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
