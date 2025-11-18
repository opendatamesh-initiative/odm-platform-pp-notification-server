package org.opendatamesh.platform.pp.notification.rest.v2.resources.notification;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "NotificationSearchOptions", description = "Search and filter options for notifications")
public class NotificationSearchOptions {

    @Schema(description = "Filter by event type")
    private String eventType;

    @Schema(description = "Filter by notification status")
    private String notificationStatus;

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

    public String getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(String notificationStatus) {
        this.notificationStatus = notificationStatus;
    }
}
