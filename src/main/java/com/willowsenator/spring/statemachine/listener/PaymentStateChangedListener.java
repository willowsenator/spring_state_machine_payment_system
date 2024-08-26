package com.willowsenator.spring.statemachine.listener;

import com.willowsenator.spring.statemachine.domain.PaymentEvent;
import com.willowsenator.spring.statemachine.domain.PaymentState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentStateChangedListener extends StateMachineListenerAdapter<PaymentState, PaymentEvent> {
    @Override
    public void stateChanged(State from, State to) {
     log.info(String.format("State changed from %s to %s", from, to));
    }
}
