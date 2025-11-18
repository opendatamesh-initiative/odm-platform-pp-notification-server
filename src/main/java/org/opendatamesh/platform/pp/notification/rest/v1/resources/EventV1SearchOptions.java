package org.opendatamesh.platform.pp.notification.rest.v1.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EventSearchOptions", description = "Search and filter options for events")
public class EventV1SearchOptions {

    @Schema(description = "Filter by event type")
    private String eventType;

    @Schema(description = "Filter by entity ID")
    private String entityId;

    public EventV1SearchOptions() {
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }
}

