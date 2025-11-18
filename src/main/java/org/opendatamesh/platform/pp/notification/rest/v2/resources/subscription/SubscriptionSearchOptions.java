package org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SubscriptionSearchOptions", description = "Search and filter options for subscriptions")
public class SubscriptionSearchOptions {

    @Schema(description = "Filter by subscription observerName")
    private String name;

    @Schema(description = "Filter by event type (observerName)")
    private String eventTypeName;

    @Schema(description = "Filter for subscriptions without event types")
    private Boolean withoutEventTypes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventTypeName() {
        return eventTypeName;
    }

    public void setEventTypeName(String eventTypeName) {
        this.eventTypeName = eventTypeName;
    }

    public Boolean getWithoutEventTypes() {
        return withoutEventTypes;
    }

    public void setWithoutEventTypes(Boolean withoutEventTypes) {
        this.withoutEventTypes = withoutEventTypes;
    }
}
