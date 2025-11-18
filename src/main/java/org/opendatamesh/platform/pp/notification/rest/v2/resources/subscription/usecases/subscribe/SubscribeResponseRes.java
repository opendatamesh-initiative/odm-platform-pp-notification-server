package org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.usecases.subscribe;

import java.util.List;

public class SubscribeResponseRes {
    private Subscription subscription;

    public SubscribeResponseRes() {
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public static class Subscription {
        String uuid;
        String observerName;
        String observerDisplayName;
        String observerBaseUrl;
        String observerApiVersion;
        List<String> eventTypes;

        public Subscription() {
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getObserverName() {
            return observerName;
        }

        public void setObserverName(String observerName) {
            this.observerName = observerName;
        }

        public String getObserverDisplayName() {
            return observerDisplayName;
        }

        public void setObserverDisplayName(String observerDisplayName) {
            this.observerDisplayName = observerDisplayName;
        }

        public String getObserverBaseUrl() {
            return observerBaseUrl;
        }

        public void setObserverBaseUrl(String observerBaseUrl) {
            this.observerBaseUrl = observerBaseUrl;
        }

        public String getObserverApiVersion() {
            return observerApiVersion;
        }

        public void setObserverApiVersion(String observerApiVersion) {
            this.observerApiVersion = observerApiVersion;
        }

        public List<String> getEventTypes() {
            return eventTypes;
        }

        public void setEventTypes(List<String> eventTypes) {
            this.eventTypes = eventTypes;
        }
    }
}
