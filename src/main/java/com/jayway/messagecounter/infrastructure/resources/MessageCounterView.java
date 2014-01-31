package com.jayway.messagecounter.infrastructure.resources;

import com.jayway.messagecounter.domain.MessageStatistics;
import com.yammer.dropwizard.views.View;

public class MessageCounterView extends View {

    private final MessageStatistics statistics;

    protected MessageCounterView(MessageStatistics statistics) {
        super("/views/messageCounter.ftl");
        this.statistics = statistics;
    }

    public MessageStatistics getStatistics() {
        return statistics;
    }
}
