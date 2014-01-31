package com.jayway.messagecounter.infrastructure.health;

import com.yammer.metrics.core.HealthCheck;

import java.util.concurrent.TimeUnit;

public class TemplateHealthCheck extends HealthCheck {
    private static final int ONE_DAY = 24;
    private final long maxMessageIdsToCache;
    private final long maxTimeToCacheMessageIds;
    private final TimeUnit timeUnit;

    public TemplateHealthCheck(long maxMessageIdsToCache, long maxTimeToCacheMessageIds, TimeUnit timeUnit) {
        super("template");
        this.maxMessageIdsToCache = maxMessageIdsToCache;
        this.maxTimeToCacheMessageIds = maxTimeToCacheMessageIds;
        this.timeUnit = timeUnit;
    }

    @Override
    protected Result check() throws Exception {
        if (maxMessageIdsToCache < 1) {
            return Result.unhealthy("Max number of messageIds to cache must be greater than or equal to 1.");
        } else if (maxTimeToCacheMessageIds < 1) {
            return Result.unhealthy("Max time to cache messageIds must be greater than or equal to 1.");
        } else if (timeUnit.toHours(maxTimeToCacheMessageIds) > ONE_DAY) {
            return Result.unhealthy("Cannot cache messageIds longer than one day.");
        }
        return Result.healthy();
    }
}