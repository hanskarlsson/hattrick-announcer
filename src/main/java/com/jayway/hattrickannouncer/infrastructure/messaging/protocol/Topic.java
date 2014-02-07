package com.jayway.hattrickannouncer.infrastructure.messaging.protocol;

import com.google.common.base.Strings;

public enum Topic {
    LOG("log"), GAME("game"), SERVICE("service");
    private static final String EXCHANGE = "lab";

    private final String routingKey;

    Topic(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public static String getLabExchange() {
        return EXCHANGE;
    }

    public static Topic valueOfIgnoreCase(String value) {
        if (Strings.isNullOrEmpty(value)) {
            return null;
        }
        try {
            return valueOf(value.toUpperCase());
        } catch (RuntimeException e) {
            return null;
        }
    }
}
