package org.opendatamesh.platform.pp.notification.rest.v1.resources;

import io.swagger.v3.oas.annotations.media.Schema;


public class EventNotificationV1SearchOptions {

    @Schema(description = "The type of the event encapsulated in the notification")
    private String eventType;

    @Schema(description = "The status of the notification")
    private EventNotificationStatusResV1 notificationStatus;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public EventNotificationStatusResV1 getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(EventNotificationStatusResV1 notificationStatus) {
        this.notificationStatus = notificationStatus;
    }
}
