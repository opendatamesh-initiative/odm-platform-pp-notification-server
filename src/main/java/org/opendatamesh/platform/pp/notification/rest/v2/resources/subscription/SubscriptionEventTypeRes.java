package org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SubscriptionEventTypeRes", description = "Resource representation of a subscription-event type mapping")
public class SubscriptionEventTypeRes {

    @Schema(description = "Unique sequence identifier of the subscription-event type link", example = "57")
    private Long sequenceId;

    @Schema(description = "The subscription associated with this event type", example = "6e1b2a41-2f24-4b56-8a3f-2149f1d456b7")
    private SubscriptionRes subscriptionRes;

    @Schema(description = "Name of the event type subscribed to", example = "DATA_PRODUCT_CREATED")
    private String eventName;

    public Long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(Long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public SubscriptionRes getSubscriptionRes() {
        return subscriptionRes;
    }

    public void setSubscriptionRes(SubscriptionRes subscriptionRes) {
        this.subscriptionRes = subscriptionRes;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
