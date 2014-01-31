package com.jayway.messagecounter.infrastructure.resources;

import com.jayway.messagecounter.domain.MessageCounter;
import com.yammer.dropwizard.config.HttpConfiguration;
import com.yammer.dropwizard.jersey.caching.CacheControl;
import com.yammer.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.concurrent.TimeUnit;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class MessageCounterResource {
    private static final String METRICS = "metrics";
    private static final String HEALTHCHECK = "healthcheck";
    private static final String ADMIN = "admin";
    private final MessageCounter messageCounter;
    private final HttpConfiguration httpConfiguration;

    public MessageCounterResource(MessageCounter messageCounter, HttpConfiguration httpConfiguration) {
        this.messageCounter = messageCounter;
        this.httpConfiguration = httpConfiguration;
    }

    @GET
    @Timed
    public EntryPointView entryPoint(@Context UriInfo uriInfo) {
        final String metricsPath;
        final String healthPath;
        // When server and admin server is executed on the same port (which is the case on Heroku) then
        // admin resources are located under /admin
        if (httpConfiguration.getAdminPort() == httpConfiguration.getPort()) {
            metricsPath = uriBuilder(uriInfo).path(ADMIN).path(METRICS).build().toString();
            healthPath = uriBuilder(uriInfo).path(ADMIN).path(HEALTHCHECK).build().toString();
        } else {
            metricsPath = uriBuilder(uriInfo).path(MessageCounterResource.class).port(httpConfiguration.getAdminPort()).path(METRICS).build().toString();
            healthPath = uriBuilder(uriInfo).path(MessageCounterResource.class).port(httpConfiguration.getAdminPort()).path(HEALTHCHECK).build().toString();
        }
        return new EntryPointView(metricsPath, healthPath);
    }

    @GET
    @Timed
    @Path("/statistics")
    @CacheControl(maxAge = 2, maxAgeUnit = TimeUnit.SECONDS)
    public MessageCounterView statistics() {
        return new MessageCounterView(messageCounter.getStatistics());
    }

    private UriBuilder uriBuilder(UriInfo uriInfo) {
        return UriBuilder.fromUri(uriInfo.getBaseUri());
    }
}