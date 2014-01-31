package com.jayway.messagecounter.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.concurrent.TimeUnit;

public class MessageCounterConfiguration extends Configuration {
    @JsonProperty
    private long maxMessageIdsToCache = 10000L;

    @JsonProperty
    private long maxTimeToCacheMessageIds = 10L;

    @NotEmpty
    @JsonProperty
    private String timeUnit = "SECONDS";

    @NotEmpty
    @JsonProperty
    private String amqpUri = "amqp://localhost:5672";

    @NotEmpty
    @JsonProperty
    private String serviceUrl;

    @NotEmpty
    @JsonProperty
    private String sourceUrl;

    @NotEmpty
    @JsonProperty
    private String creator;

    @NotEmpty
    @JsonProperty
    private String description;

    public long getMaxMessageIdsToCache() {
        return maxMessageIdsToCache;
    }

    public long getMaxTimeToCacheMessageIds() {
        return maxTimeToCacheMessageIds;
    }

    public TimeUnit getTimeUnit() {
        return TimeUnit.valueOf(timeUnit);
    }

    public String getAmqpUri() {
        return amqpUri;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getCreator() {
        return creator;
    }

    public String getDescription() {
        return description;
    }
}