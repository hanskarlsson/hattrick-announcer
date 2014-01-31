package com.jayway.messagecounter.boot;

import com.jayway.messagecounter.domain.MessageCounter;
import com.jayway.messagecounter.infrastructure.config.MessageCounterConfiguration;
import com.jayway.messagecounter.infrastructure.health.TemplateHealthCheck;
import com.jayway.messagecounter.infrastructure.messaging.RabbitMQConsumer;
import com.jayway.messagecounter.infrastructure.messaging.RabbitMQServiceRegistrar;
import com.jayway.messagecounter.infrastructure.messaging.protocol.MessageCounterSettings;
import com.jayway.messagecounter.infrastructure.resources.MessageCounterResource;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.views.ViewBundle;

public class BootstrapService extends Service<MessageCounterConfiguration> {

    public static void main(String[] args) throws Exception {
        new BootstrapService().run(args);
    }

    @Override
    public void initialize(Bootstrap<MessageCounterConfiguration> bootstrap) {
        bootstrap.setName(MessageCounterSettings.APP_ID);
        bootstrap.addBundle(new ViewBundle());
    }

    @Override
    public void run(MessageCounterConfiguration config, Environment environment) {
        MessageCounter messageCounter = new MessageCounter(config.getMaxMessageIdsToCache(), config.getMaxTimeToCacheMessageIds(), config.getTimeUnit());
        environment.addResource(new MessageCounterResource(messageCounter));
        environment.addHealthCheck(new TemplateHealthCheck(config));
        environment.manage(new RabbitMQConsumer(config.getAmqpUri(), messageCounter));
        environment.manage(new RabbitMQServiceRegistrar(config));
    }
}