package com.jayway.messagecounter.infrastructure.resources;

import com.yammer.dropwizard.views.View;

public class EntryPointView extends View {

    public EntryPointView() {
        super("/views/index.ftl");
    }
}
