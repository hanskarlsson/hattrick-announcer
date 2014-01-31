package com.jayway.messagecounter.boot;

import com.jayway.messagecounter.domain.MessageCounter;
import com.jayway.messagecounter.infrastructure.config.MessageCounterConfiguration;
import com.jayway.messagecounter.infrastructure.health.TemplateHealthCheck;
import com.jayway.messagecounter.infrastructure.messaging.RabbitMQConsumer;
import com.jayway.messagecounter.infrastructure.resources.MessageCounterResource;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

public class BootstrapService extends Service<MessageCounterConfiguration> {

    public static void main(String[] args) throws Exception {
        new BootstrapService().run(args);
    }

    @Override
    public void initialize(Bootstrap<MessageCounterConfiguration> bootstrap) {
        bootstrap.setName("message-counter");
    }

    @Override
    public void run(MessageCounterConfiguration configuration, Environment environment) {
        MessageCounter messageCounter = new MessageCounter(configuration.getMaxMessageIdsToCache(), configuration.getMaxTimeToCacheMessageIds(), configuration.getTimeUnit());
        environment.addResource(new MessageCounterResource(messageCounter));
        environment.addHealthCheck(new TemplateHealthCheck(configuration.getMaxMessageIdsToCache(), configuration.getMaxTimeToCacheMessageIds(), configuration.getTimeUnit()));
        environment.manage(new RabbitMQConsumer(configuration.getAmqpUri(), messageCounter));
    }
}