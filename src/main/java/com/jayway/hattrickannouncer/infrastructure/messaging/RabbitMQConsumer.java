package com.jayway.hattrickannouncer.infrastructure.messaging;

import com.jayway.hattrickannouncer.domain.MessageCounterService;
import com.jayway.hattrickannouncer.infrastructure.messaging.protocol.HattrickAnnouncer;
import com.jayway.hattrickannouncer.infrastructure.messaging.protocol.Topic;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.yammer.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class RabbitMQConsumer implements Managed {
    private static final Logger log = LoggerFactory.getLogger(RabbitMQConsumer.class);

    private static final String ALL_ROUTES = "#";
    private static final String QUEUE_NAME = HattrickAnnouncer.APP_ID + "-queue";

    private final String amqpUri;
    private final MessageCounterService messageCounterService;

    private ExecutorService executorService;
    private volatile boolean isRunning = true;

    public RabbitMQConsumer(String amqpUri, MessageCounterService messageCounterService) {
        this.amqpUri = amqpUri;
        this.messageCounterService = messageCounterService;
    }

    @Override
    public void start() throws Exception {
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                startRabbitConsumer();
            }
        });
    }

    @Override
    public void stop() throws Exception {
        isRunning = false;
        boolean isTerminated = executorService.awaitTermination(3, TimeUnit.SECONDS);
        if (!isTerminated) {
            executorService.shutdownNow();
        }
    }

    private void startRabbitConsumer() {
        Connection connection = null;
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri(amqpUri);

            connection = factory.newConnection();
            Channel channel = connection.createChannel();
            //channel.exchangeDeclare("lab", "topic" );
            String queueName = channel.queueDeclare(QUEUE_NAME, true, false, false, null).getQueue();
            channel.queueBind(queueName, Topic.getLabExchange(), ALL_ROUTES);

            QueueingConsumer consumer = new QueueingConsumer(channel);
            channel.basicConsume(queueName, true, consumer);

            log.info("RabbitMQ consumer is waiting for messages.");
            while (isRunning) {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery(1000);
                if (delivery != null) {
                    String routingKey = delivery.getEnvelope().getRoutingKey();

                    log.debug("Received message on routing key {}.", routingKey);
                    messageCounterService.messageReceived(delivery.getProperties().getMessageId(), routingKey);
                }
            }
        } catch (Exception e) {
            log.error("RabbitMQ consumer caught exception", e);
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
