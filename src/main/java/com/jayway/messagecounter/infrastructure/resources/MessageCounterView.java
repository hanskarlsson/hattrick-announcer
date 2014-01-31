package com.jayway.messagecounter.infrastructure.resources;

import com.yammer.dropwizard.views.View;

public class MessageCounterView extends View {

    private final long count;

    protected MessageCounterView(long count) {
        super("/views/messageCounter.ftl");
        this.count = count;
    }

    public long getCount() {
        return count;
    }
}
