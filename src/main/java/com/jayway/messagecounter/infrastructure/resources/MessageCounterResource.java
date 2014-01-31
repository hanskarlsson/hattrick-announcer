package com.jayway.messagecounter.infrastructure.resources;

import com.jayway.messagecounter.domain.MessageCounter;
import com.yammer.dropwizard.jersey.caching.CacheControl;
import com.yammer.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.TimeUnit;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class MessageCounterResource {
    private final MessageCounter messageCounter;

    public MessageCounterResource(MessageCounter messageCounter) {
        this.messageCounter = messageCounter;
    }

    @GET
    @Timed
    @CacheControl(maxAge = 2, maxAgeUnit = TimeUnit.SECONDS)
    public MessageCounterView statistics() {
        return new MessageCounterView(messageCounter.getStatistics());
    }
}