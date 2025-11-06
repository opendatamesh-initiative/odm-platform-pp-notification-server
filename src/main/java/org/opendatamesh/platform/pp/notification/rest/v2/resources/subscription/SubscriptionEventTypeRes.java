package org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SubscriptionEventTypeRes", description = "Resource representation of a subscription-event type mapping")
public class SubscriptionEventTypeRes {

    @Schema(description = "Type of the event type subscribed to", example = "DATA_PRODUCT_CREATED")
    private String eventType;

    public SubscriptionEventTypeRes() {
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
