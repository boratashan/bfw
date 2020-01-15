package com.mybaas.currencyanalyzer;

import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.Map;

@EnableJms
public class Listener {
    @JmsListener(destination = "event")
    public void receiveMessage(final Message jsonMessage) throws JMSException {
        System.out.println("Received message " + jsonMessage);
    }
}
