package com.jayway.hattrickannouncer.boot;

import com.jayway.hattrickannouncer.domain.MessageCounterService;
import com.jayway.hattrickannouncer.infrastructure.config.HattrickAnnouncerConfiguration;
import com.jayway.hattrickannouncer.infrastructure.health.TemplateHealthCheck;
import com.jayway.hattrickannouncer.infrastructure.messaging.RabbitMQConsumer;
import com.jayway.hattrickannouncer.infrastructure.messaging.RabbitMQServiceRegistrar;
import com.jayway.hattrickannouncer.infrastructure.messaging.protocol.HattrickAnnouncer;
import com.jayway.hattrickannouncer.infrastructure.resources.HattrickAnnouncerResource;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.views.ViewBundle;

public class BootstrapService extends Service<HattrickAnnouncerConfiguration> {

    public static void main(String[] args) throws Exception {
        new BootstrapService().run(args);
    }

    @Override
    public void initialize(Bootstrap<HattrickAnnouncerConfiguration> bootstrap) {
        bootstrap.setName(HattrickAnnouncer.APP_ID);
        bootstrap.addBundle(new ViewBundle());
    }

    @Override
    public void run(HattrickAnnouncerConfiguration config, Environment environment) {
        MessageCounterService messageCounterService = new MessageCounterService(config.getMaxMessageIdsToCache(), config.getMaxTimeToCacheMessageIds(), config.getTimeUnit());
        environment.addResource(new HattrickAnnouncerResource(messageCounterService, config.getHttpConfiguration()));
        environment.addHealthCheck(new TemplateHealthCheck(config));
        environment.manage(new RabbitMQConsumer(config.getAmqpUri(), messageCounterService));
        environment.manage(new RabbitMQServiceRegistrar(config));
    }
}