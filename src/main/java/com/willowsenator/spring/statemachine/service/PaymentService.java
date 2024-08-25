package com.willowsenator.spring.statemachine.service;

import com.willowsenator.spring.statemachine.domain.Payment;
import com.willowsenator.spring.statemachine.domain.PaymentEvent;
import com.willowsenator.spring.statemachine.domain.PaymentState;
import org.springframework.statemachine.StateMachine;

import java.util.UUID;

public interface PaymentService {
    Payment newPayment(Payment payment);
    StateMachine<PaymentState, PaymentEvent> preAuth(UUID paymentId);
    StateMachine<PaymentState, PaymentEvent>  authorizePayment(UUID paymentId);
    StateMachine<PaymentState, PaymentEvent>  declineAuth(UUID paymentId);
}
