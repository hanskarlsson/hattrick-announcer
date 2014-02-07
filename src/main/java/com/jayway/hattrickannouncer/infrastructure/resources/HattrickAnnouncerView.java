package com.jayway.hattrickannouncer.infrastructure.resources;

import com.jayway.hattrickannouncer.domain.MessageStatistics;
import com.yammer.dropwizard.views.View;

public class HattrickAnnouncerView extends View {

    private final MessageStatistics statistics;

    protected HattrickAnnouncerView(MessageStatistics statistics) {
        super("/views/messageCounter.ftl");
        this.statistics = statistics;
    }

    public MessageStatistics getStatistics() {
        return statistics;
    }
}
