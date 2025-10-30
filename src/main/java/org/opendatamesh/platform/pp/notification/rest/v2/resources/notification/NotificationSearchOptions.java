package org.opendatamesh.platform.pp.notification.rest.v2.resources.notification;

import io.swagger.v3.oas.annotations.media.Schema;

public class NotificationSearchOptions {

    @Schema(description = "Filter by event type")
    private String eventType;

    @Schema(description = "Filter by notification status")
    private NotificationStatus notificationStatus;

    @Schema(description = "Filter by subscription UUID")
    private String subscriptionUuid;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getSubscriptionUuid() {
        return subscriptionUuid;
    }

    public void setSubscriptionUuid(String subscriptionUuid) {
        this.subscriptionUuid = subscriptionUuid;
    }

    public NotificationStatus getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(NotificationStatus notificationStatus) {
        this.notificationStatus = notificationStatus;
    }
}
