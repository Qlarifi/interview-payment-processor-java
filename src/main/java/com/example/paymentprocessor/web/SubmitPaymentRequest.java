package com.example.paymentprocessor.web;

import java.math.BigDecimal;

public record SubmitPaymentRequest(String paymentId, BigDecimal amount) {
}
