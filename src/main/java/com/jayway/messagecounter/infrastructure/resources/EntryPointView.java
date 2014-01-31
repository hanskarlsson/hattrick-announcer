package com.jayway.messagecounter.infrastructure.resources;

import com.yammer.dropwizard.views.View;

public class EntryPointView extends View {

    private final String metricsUrl;
    private final String healthUrl;

    public EntryPointView(String metricsPath, String healthPath) {
        super("/views/index.ftl");
        this.metricsUrl = metricsPath;
        this.healthUrl = healthPath;
    }

    public String getMetricsUrl() {
        return metricsUrl;
    }

    public String getHealthUrl() {
        return healthUrl;
    }
}
