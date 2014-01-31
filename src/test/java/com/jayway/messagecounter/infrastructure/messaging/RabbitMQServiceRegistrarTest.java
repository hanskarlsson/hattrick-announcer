package com.jayway.messagecounter.infrastructure.messaging;

import com.jayway.messagecounter.infrastructure.config.MessageCounterConfiguration;
import com.jayway.messagecounter.infrastructure.messaging.protocol.MessageCounterSettings;
import com.jayway.messagecounter.infrastructure.messaging.protocol.MessageType;
import com.jayway.messagecounter.infrastructure.messaging.protocol.Topic;
import com.rabbitmq.client.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.rabbitmq.client.QueueingConsumer.Delivery;
import static org.assertj.core.api.Assertions.assertThat;

public class RabbitMQServiceRegistrarTest {

    private static final String AMQP_URI = "amqp://localhost:5672";

    RabbitMQServiceRegistrar tested;
    Connection connection;
    QueueingConsumer consumer;

    @Before public void
    given_service_registrar_is_initialized() {
        tested = new RabbitMQServiceRegistrar(new MyMessageCounterConfiguration(AMQP_URI));
    }

    @Before public void
    given_test_amqp_client_is_connected() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(AMQP_URI);

        connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, Topic.getLabExchange(), Topic.SERVICE.getRoutingKey());

        consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName, true, consumer);
    }

    @After public void
    close_connection_to_amqp() throws IOException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test public void
    sends_service_online_message_when_starting_up() throws Exception {
        // When
        tested.start();

        // Then
        Delivery delivery = consumer.nextDelivery(3000);
        assertThat(delivery).describedAs("Message was not delivered within 3 seconds").isNotNull();

        AMQP.BasicProperties properties = delivery.getProperties();
        assertThat(properties.getAppId()).isEqualTo(MessageCounterSettings.APP_ID);
        assertThat(properties.getHeaders().get("streamId").toString()).isEqualTo(MessageCounterSettings.APP_ID);
        assertThat(properties.getMessageId()).isNotEmpty();
        assertThat(properties.getTimestamp()).isNotNull();
        assertThat(properties.getType()).isEqualTo(MessageType.SERVICE_ONLINE);
    }

    @Test public void
    sends_service_offline_message_when_shutting_down() throws Exception {
        // When
        tested.stop();

        // Then
        Delivery delivery = consumer.nextDelivery(3000);
        assertThat(delivery).describedAs("Message was not delivered within 3 seconds").isNotNull();

        AMQP.BasicProperties properties = delivery.getProperties();
        assertThat(properties.getAppId()).isEqualTo(MessageCounterSettings.APP_ID);
        assertThat(properties.getHeaders().get("streamId").toString()).isEqualTo(MessageCounterSettings.APP_ID);
        assertThat(properties.getMessageId()).isNotEmpty();
        assertThat(properties.getTimestamp()).isNotNull();
        assertThat(properties.getType()).isEqualTo(MessageType.SERVICE_OFFLINE);
    }

    private static class MyMessageCounterConfiguration extends MessageCounterConfiguration {
        private final String amqpUri;

        public MyMessageCounterConfiguration(String amqpUri) {
            this.amqpUri = amqpUri;
        }

        @Override
        public String getAmqpUri() {
            return amqpUri;
        }

        @Override
        public String getServiceUrl() {
            return "http://service-url.com";
        }

        @Override
        public String getSourceUrl() {
            return "http://source-url.com";
        }

        @Override
        public String getCreator() {
            return "creator";
        }

        @Override
        public String getDescription() {
            return "description";
        }
    }
}
