package com.example.paymentprocessor.repository;

import com.example.paymentprocessor.model.Payment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByPaymentId(String paymentId);

    List<Payment> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
