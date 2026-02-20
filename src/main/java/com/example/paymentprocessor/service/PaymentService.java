package com.example.paymentprocessor.service;

import com.example.paymentprocessor.model.Payment;
import com.example.paymentprocessor.model.PaymentMessage;
import com.example.paymentprocessor.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment processPayment(PaymentMessage message) {
        Payment payment = new Payment(message.getPaymentId(), message.getAmount());
        payment = paymentRepository.save(payment);
        log.info("payment {} created — id {}", message.getPaymentId(), payment.getId());
        return payment;
    }

    public List<Payment> getRecentPayments(int limit) {
        return paymentRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit));
    }
}
