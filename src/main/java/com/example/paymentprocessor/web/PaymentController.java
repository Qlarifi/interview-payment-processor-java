package com.example.paymentprocessor.web;

import com.example.paymentprocessor.model.Payment;
import com.example.paymentprocessor.model.PaymentMessage;
import com.example.paymentprocessor.queue.PaymentMessageQueue;
import com.example.paymentprocessor.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final int DEFAULT_RECENT_LIMIT = 10;
    private static final int MAX_RECENT_LIMIT = 100;

    private final PaymentMessageQueue queue;
    private final PaymentService paymentService;

    public PaymentController(PaymentMessageQueue queue, PaymentService paymentService) {
        this.queue = queue;
        this.paymentService = paymentService;
    }

    /**
     * Submit a payment for async processing. The message is queued and
     * workers will process it.
     */
    @PostMapping("/submit")
    public ResponseEntity<Map<String, String>> submit(@RequestBody SubmitPaymentRequest request) {
        PaymentMessage message = PaymentMessage.of(request.paymentId(), request.amount());
        queue.submit(message);
        return ResponseEntity.accepted()
                .body(Map.of(
                        "status", "accepted",
                        "paymentId", request.paymentId()
                ));
    }

    /**
     * Get the most recent payments, ordered by creation time descending.
     *
     * @param limit optional; number of payments to return (default 10, max 100)
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Payment>> recent(
            @RequestParam(defaultValue = "" + DEFAULT_RECENT_LIMIT) int limit) {
        int capped = Math.min(Math.max(1, limit), MAX_RECENT_LIMIT);
        return ResponseEntity.ok(paymentService.getRecentPayments(capped));
    }
}
