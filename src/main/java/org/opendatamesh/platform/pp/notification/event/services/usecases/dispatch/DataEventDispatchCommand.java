package org.opendatamesh.platform.pp.notification.event.services.usecases.dispatch;

import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.EventType;

public class DataEventDispatchCommand {

    private final EventType eventType;
    private final String resourceType;
    private final String resourceIdentifier;
    private final String eventContent;
    private final String eventTypeVersion;

    public DataEventDispatchCommand(EventType eventType,
                                    String resourceType,
                                    String resourceIdentifier,
                                    String eventContent,
                                    String eventTypeVersion) {
        this.eventType = eventType;
        this.resourceType = resourceType;
        this.resourceIdentifier = resourceIdentifier;
        this.eventContent = eventContent;
        this.eventTypeVersion = eventTypeVersion;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getResourceIdentifier() {
        return resourceIdentifier;
    }

    public String getEventContent() {
        return eventContent;
    }

    public String getEventTypeVersion() {
        return eventTypeVersion;
    }
}


