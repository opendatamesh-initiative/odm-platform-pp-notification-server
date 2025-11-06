package org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription;

import io.swagger.v3.oas.annotations.media.Schema;
import org.opendatamesh.platform.pp.notification.utils.resources.VersionedRes;

import java.util.List;

@Schema(name = "SubscriptionRes", description = "Resource representation of a subscription entity")
public class SubscriptionRes extends VersionedRes {

    @Schema(description = "Unique identifier (UUID) of the subscription", example = "6e1b2a41-2f24-4b56-8a3f-2149f1d456b7")
    private String uuid;

    @Schema(description = "Internal observerName of the subscription")
    private String name;

    @Schema(description = "Human-readable display observerName of the subscription")
    private String displayName;

    @Schema(description = "Base URL of the observer server associated with this subscription", example = "https://observer.blindata.dev/api/v1")
    private String observerServerBaseUrl;

    @Schema(description = "API version of the observer server", example = "v1")
    private String observerApiVersion;

    @Schema(description = "List of event types this subscription is subscribed to.")
    private List<SubscriptionEventTypeRes> eventTypes;

    public SubscriptionRes() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getObserverServerBaseUrl() {
        return observerServerBaseUrl;
    }

    public void setObserverServerBaseUrl(String observerServerBaseUrl) {
        this.observerServerBaseUrl = observerServerBaseUrl;
    }

    public String getObserverApiVersion() {
        return observerApiVersion;
    }

    public void setObserverApiVersion(String observerApiVersion) {
        this.observerApiVersion = observerApiVersion;
    }

    public List<SubscriptionEventTypeRes> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(List<SubscriptionEventTypeRes> eventTypes) {
        this.eventTypes = eventTypes;
    }
}
