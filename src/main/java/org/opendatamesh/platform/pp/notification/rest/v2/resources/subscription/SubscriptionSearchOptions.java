package org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription;

import io.swagger.v3.oas.annotations.media.Schema;

public class SubscriptionSearchOptions {

    @Schema(description = "Filter by subscription name")
    private String name;

    @Schema(description = "Filter by event type")
    private SubscriptionEventTypeRes eventType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SubscriptionEventTypeRes getEventType() {
        return eventType;
    }

    public void setEventType(SubscriptionEventTypeRes eventType) {
        this.eventType = eventType;
    }
}
