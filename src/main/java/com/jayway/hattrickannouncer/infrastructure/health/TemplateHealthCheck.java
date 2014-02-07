package com.jayway.hattrickannouncer.infrastructure.health;

import com.jayway.hattrickannouncer.infrastructure.config.HattrickAnnouncerConfiguration;
import com.yammer.metrics.core.HealthCheck;

import java.util.concurrent.TimeUnit;

public class TemplateHealthCheck extends HealthCheck {
    private static final int ONE_DAY = 24;
    private final long maxMessageIdsToCache;
    private final long maxTimeToCacheMessageIds;
    private final TimeUnit timeUnit;
    private final String serviceUrl;
    private final String sourceUrl;

    public TemplateHealthCheck(HattrickAnnouncerConfiguration configuration) {
        super("template");
        this.maxMessageIdsToCache = configuration.getMaxMessageIdsToCache();
        this.maxTimeToCacheMessageIds = configuration.getMaxTimeToCacheMessageIds();
        this.timeUnit = configuration.getTimeUnit();
        this.serviceUrl = configuration.getServiceUrl();
        this.sourceUrl = configuration.getSourceUrl();
    }

    @Override
    protected Result check() throws Exception {
        final Result result;
        if (maxMessageIdsToCache < 1) {
            result = Result.unhealthy("Max number of messageIds to cache must be greater than or equal to 1.");
        } else if (maxTimeToCacheMessageIds < 1) {
            result = Result.unhealthy("Max time to cache messageIds must be greater than or equal to 1.");
        } else if (timeUnit.toHours(maxTimeToCacheMessageIds) > ONE_DAY) {
            result = Result.unhealthy("Cannot cache messageIds longer than one day.");
        } else if (!serviceUrl.startsWith("http")) {
            result = Result.unhealthy("Service URL must be a valid http address.");
        } else if (!sourceUrl.startsWith("http")) {
            result = Result.unhealthy("Source URL must be a valid http address.");
        } else {
            result = Result.healthy();
        }
        return result;
    }
}