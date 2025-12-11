package org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.usecases.subscribe;

import java.util.List;

public class SubscribeCommandRes {
    String name;
    String displayName;
    String observerBaseUrl;
    String observerApiVersion;
    List<String> eventTypes;

    public SubscribeCommandRes() {
    }

    public String getObserverApiVersion() {
        return observerApiVersion;
    }

    public void setObserverApiVersion(String observerApiVersion) {
        this.observerApiVersion = observerApiVersion;
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

    public String getObserverBaseUrl() {
        return observerBaseUrl;
    }

    public void setObserverBaseUrl(String observerBaseUrl) {
        this.observerBaseUrl = observerBaseUrl;
    }

    public List<String> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(List<String> eventTypes) {
        this.eventTypes = eventTypes;
    }
}
