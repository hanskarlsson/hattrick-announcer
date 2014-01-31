package com.jayway.messagecounter.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.messagecounter.infrastructure.config.MessageCounterConfiguration;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class RabbitMQServiceRegistrar implements Managed {
    private static final Logger log = LoggerFactory.getLogger(RabbitMQServiceRegistrar.class);

    private final String amqpUri;
    private final String serviceUrl;
    private final String sourceUrl;
    private final String creator;
    private final String description;

    public RabbitMQServiceRegistrar(MessageCounterConfiguration config) {
        this.amqpUri = config.getAmqpUri();
        this.serviceUrl = config.getServiceUrl();
        this.sourceUrl = config.getSourceUrl();
        this.creator = config.getCreator();
        this.description = config.getDescription();
    }

    @Override
    public void start() throws Exception {
        Message message = Messages.serviceOnline(MessageCounterSettings.APP_ID, description, creator, serviceUrl, sourceUrl);
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

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri(amqpUri);

            connection = factory.newConnection();
            Channel channel = connection.createChannel();

            Map<String, Object> headers = new HashMap<>(message.getMeta());
            headers.keySet().removeAll(asList("messageId", "appId", "timestamp", "type"));

            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().appId(message.<String>meta("appId")).
                    messageId(message.<String>meta("messageId")).type(message.<String>meta("type")).timestamp(new Date(message.<Long>meta("timestamp"))).
                    headers(headers).contentType("application/json").build();
            byte[] bytes = new ObjectMapper().writeValueAsBytes(message.getBody());

            channel.basicPublish(Topic.getLabExchange(), routingKey, properties, bytes);
        } catch (Exception e) {
            log.error(format("Error when sending message to RabbitMQ. Message was: %s", message), e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    log.error("Error closing connection", e);
                }
            }
        }
    }
}
