package com.jayway.hattrickannouncer.domain;

import com.jayway.hattrickannouncer.infrastructure.messaging.protocol.Topic;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class HattrickAnnouncerServiceTest {

    private MessageCounterService messageCounterService;

    @Before public void
    given_message_counter_is_initialized() {
        messageCounterService = new MessageCounterService(100, 10, TimeUnit.SECONDS);
    }

    @Test public void
    increases_counter_when_message_id_not_found_in_cache() {
        // When
        messageCounterService.messageReceived("ABCD", Topic.LOG.getRoutingKey());

        // Then
        assertThat(messageCounterService.getStatistics().getTotal()).isEqualTo(1);
    }

    @Test public void
    increases_counter_when_message_id_not_found_in_cache_and_routing_key_is_null() {
        // When
        messageCounterService.messageReceived("ABCD", null);

        // Then
        assertThat(messageCounterService.getStatistics().getTotal()).isEqualTo(1);
    }

    @Test public void
    increases_counter_when_message_id_is_undefined() {
        // When
        messageCounterService.messageReceived(null, Topic.LOG.getRoutingKey());

        // Then
        assertThat(messageCounterService.getStatistics().getTotal()).isEqualTo(1);
    }

    @Test public void
    doesnt_increase_counter_when_message_id_is_found_in_cache() {
        // Given
        messageCounterService.messageReceived("ABCD", Topic.LOG.getRoutingKey());

        // When
        messageCounterService.messageReceived("ABCD", Topic.LOG.getRoutingKey());

        // Then
        assertThat(messageCounterService.getStatistics().getTotal()).isEqualTo(1);
    }
}
