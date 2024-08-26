package com.willowsenator.spring.statemachine.service;

import com.willowsenator.spring.statemachine.domain.Payment;
import com.willowsenator.spring.statemachine.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
class PaymentServiceImplTest {
    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder().amount(new BigDecimal("10.2")).build();
    }

    @Transactional
    @Test
    void preAth() {
        var savedPayment = paymentService.newPayment(payment);

        System.out.println("Shoud be NEW");
        System.out.println(savedPayment.getState());

        paymentService.preAuth(savedPayment.getId());
        var preAuthPayment = paymentRepository.getReferenceById(savedPayment.getId());

        System.out.println("Should be PRE_AUTH or PRE_AUTH_ERROR");
        System.out.println(preAuthPayment.getState());

        System.out.println(preAuthPayment);
    }

}