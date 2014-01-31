package com.jayway.messagecounter.domain;

import com.jayway.messagecounter.infrastructure.messaging.protocol.Topic;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class MessageCounterTest {

    private MessageCounter messageCounter;

    @Before public void
    given_message_counter_is_initialized() {
        messageCounter = new MessageCounter(100, 10, TimeUnit.SECONDS);
    }

    @Test public void
    increases_counter_when_message_id_not_found_in_cache() {
        // When
        messageCounter.messageReceived("ABCD", Topic.LOG.getRoutingKey());

        // Then
        assertThat(messageCounter.getStatistics().getTotal()).isEqualTo(1);
    }

    @Test public void
    increases_counter_when_message_id_not_found_in_cache_and_routing_key_is_null() {
        // When
        messageCounter.messageReceived("ABCD", null);

        // Then
        assertThat(messageCounter.getStatistics().getTotal()).isEqualTo(1);
    }

    @Test public void
    increases_counter_when_message_id_is_undefined() {
        // When
        messageCounter.messageReceived(null, Topic.LOG.getRoutingKey());

        // Then
        assertThat(messageCounter.getStatistics().getTotal()).isEqualTo(1);
    }

    @Test public void
    doesnt_increase_counter_when_message_id_is_found_in_cache() {
        // Given
        messageCounter.messageReceived("ABCD", Topic.LOG.getRoutingKey());

        // When
        messageCounter.messageReceived("ABCD", Topic.LOG.getRoutingKey());

        // Then
        assertThat(messageCounter.getStatistics().getTotal()).isEqualTo(1);
    }
}
