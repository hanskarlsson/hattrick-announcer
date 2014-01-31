package com.jayway.messagecounter.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.messagecounter.infrastructure.messaging.protocol.Message;
import com.jayway.messagecounter.infrastructure.messaging.protocol.MessageCounterSettings;
import com.jayway.messagecounter.infrastructure.messaging.protocol.Messages;
import com.jayway.messagecounter.infrastructure.messaging.protocol.Topic;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.yammer.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RabbitMQServiceRegistrar implements Managed {
    private static final Logger log = LoggerFactory.getLogger(RabbitMQServiceRegistrar.class);

    private final String amqpUri;

    public RabbitMQServiceRegistrar(String amqpUri) {
        this.amqpUri = amqpUri;
    }

    @Override
    public void start() throws Exception {
        Message message = Messages.serviceOnline(MessageCounterSettings.APP_ID, "Counts all messages sent in the lab", "Johan Haleby", "http://lab-message-counter.herokuapp.com", "https://github.com/johanhaleby/messagecounter-lab");
        sendMessage(message, Topic.SERVICE.getRoutingKey());
        log.info("Registered service {}", MessageCounterSettings.APP_ID);
    }

    @Override
    public void stop() throws Exception {
        Message message = Messages.serviceOffline(MessageCounterSettings.APP_ID);
        sendMessage(message, Topic.SERVICE.getRoutingKey());
        log.info("Unregistered service {}", MessageCounterSettings.APP_ID);
    }

    private void sendMessage(Message message, String routingKey) {
        // Note that you normally shouldn't create a new connection factory for each message!
        // It works fine here since we don't need to maintain a connection during the entire life-cycle
        // of this application. (It only sends one message when started and one when ended).
        Connection connection = null;
        Channel channel;
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(amqpUri);

            connection = factory.newConnection();
            channel = connection.createChannel();

            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().headers(message.getMeta()).contentType("application/json").build();
            byte[] bytes = new ObjectMapper().writeValueAsBytes(message.getBody());

            channel.basicPublish(Topic.getLabExchange(), routingKey, properties, bytes);
        } catch (Exception e) {
            log.error("Error when sending message to RabbitMQ", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ignore) {
                }
            }
        }
    }
}
