package com.jayway.messagecounter.domain;

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
        messageCounter.messageReceived("ABCD");

        // Then
        assertThat(messageCounter.currentNumberOfReceivedMessages()).isEqualTo(1);
    }

    @Test public void
    increases_counter_when_message_id_is_undefined() {
        // When
        messageCounter.messageReceived(null);

        // Then
        assertThat(messageCounter.currentNumberOfReceivedMessages()).isEqualTo(1);
    }

    @Test public void
    doesnt_increase_counter_when_message_id_is_found_in_cache() {
        // Given
        messageCounter.messageReceived("ABCD");

        // When
        messageCounter.messageReceived("ABCD");

        // Then
        assertThat(messageCounter.currentNumberOfReceivedMessages()).isEqualTo(1);
    }
}
