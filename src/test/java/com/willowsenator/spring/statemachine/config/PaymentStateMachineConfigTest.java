package com.willowsenator.spring.statemachine.config;

import com.willowsenator.spring.statemachine.domain.PaymentEvent;
import com.willowsenator.spring.statemachine.domain.PaymentState;
import com.willowsenator.spring.statemachine.listener.PaymentStateChangedListener;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.config.StateMachineFactory;
import reactor.core.publisher.Mono;

import java.util.UUID;


@SpringBootTest
class PaymentStateMachineConfigTest {
    @Autowired
    StateMachineFactory<PaymentState, PaymentEvent> factory;

    @Autowired
    PaymentStateChangedListener listener;

    @Test
    void testNewStateMachine() {
        var sm = factory.getStateMachine(UUID.randomUUID());
        sm.startReactively().block();

        System.out.println(sm.getState().toString());

        var preAuthEventMono = Mono.just(MessageBuilder.withPayload(PaymentEvent.PRE_AUTHORIZE).build());
        var resultPreAuthEventFlux = sm.sendEvent(preAuthEventMono);

        resultPreAuthEventFlux.subscribe(result -> {
           var state = result.getRegion().getState();
           System.out.println(state.toString());
       });

       var preAuthApprovedEventMono = Mono.just(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_APPROVED).build());
       var resultPreAuthApprovedEventFlux = sm.sendEvent(preAuthApprovedEventMono);

       resultPreAuthApprovedEventFlux.subscribe(result -> {
           var state = result.getRegion().getState();
           System.out.println(state.toString());
       });

       var preAuthDeclinedEventMono = Mono.just(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_DECLINED).build());
       var resultPreAuthDeclinedEventFlux = sm.sendEvent(preAuthDeclinedEventMono);

         resultPreAuthDeclinedEventFlux.subscribe(result -> {
              var state = result.getRegion().getState();
              System.out.println(state.toString());
         });
    }
}