package org.opendatamesh.platform.pp.notification.rest.v2.resources.event;

import io.swagger.v3.oas.annotations.media.Schema;

public class EventSearchOptions {

    @Schema(description = "Filter by event type")
    private EventType eventType;

    @Schema(description = "Filter by resource type")
    private String resourceType;

    @Schema(description = "Filter by resource UUID")
    private String resourceUuid;

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceUuid() {
        return resourceUuid;
    }

    public void setResourceUuid(String resourceUuid) {
        this.resourceUuid = resourceUuid;
    }
}
