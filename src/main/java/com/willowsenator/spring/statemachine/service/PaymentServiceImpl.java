package com.willowsenator.spring.statemachine.service;

import com.willowsenator.spring.statemachine.domain.Payment;
import com.willowsenator.spring.statemachine.domain.PaymentEvent;
import com.willowsenator.spring.statemachine.domain.PaymentState;
import com.willowsenator.spring.statemachine.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    public static final String PAYMENT_ID_HEADER = "payment_id";
    private final PaymentRepository paymentRepository;
    private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;

    @Override
    public Payment newPayment(Payment payment) {
        payment.setState(PaymentState.NEW);
        return paymentRepository.save(payment);
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuth(UUID paymentId) {
        var sm = build(paymentId);
        sendEvent(paymentId, sm, PaymentEvent.PRE_AUTHORIZE).blockFirst();
        return null;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> authorizePayment(UUID paymentId) {
        var sm = build(paymentId);
        sendEvent(paymentId, sm, PaymentEvent.AUTH_APPROVED).blockFirst();
        return null;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> declineAuth(UUID paymentId) {
        var sm = build(paymentId);
        sendEvent(paymentId, sm, PaymentEvent.AUTH_DECLINED).blockFirst();
        return null;
    }

    private Flux<StateMachineEventResult<PaymentState, PaymentEvent>> sendEvent(
                                                                                UUID paymentId,
                                                                                StateMachine<PaymentState,
                                                                                PaymentEvent> sm,
                                                                                PaymentEvent event
                                                                                ) {
        var msg = MessageBuilder.withPayload(event)
                .setHeader(PAYMENT_ID_HEADER, paymentId)
                .build();
        return sm.sendEvent(Mono.just(msg));
    }

    private StateMachine<PaymentState, PaymentEvent> build(UUID paymentId) {
        var payment = paymentRepository.getReferenceById(paymentId);
        var sm = stateMachineFactory.getStateMachine(paymentId);
        startStateMachine(sm);
        resetStateMachineContext(sm, payment);

        stopStateMachine(sm);
        return sm;
    }

    private static void stopStateMachine(StateMachine<PaymentState, PaymentEvent> sm) {
        sm.startReactively().block();
    }

    private static void startStateMachine(StateMachine<PaymentState, PaymentEvent> sm) {
        sm.stopReactively().block();
    }

    private static void resetStateMachineContext(StateMachine<PaymentState, PaymentEvent> sm, Payment payment) {
        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> sma.resetStateMachineReactively(new DefaultStateMachineContext<>(payment.getState(), null, null, null)));
    }
}
