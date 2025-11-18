package org.opendatamesh.platform.pp.notification.rest;

public enum RoutesV2 {

    EVENTS("/api/v2/pp/notification/events"),
    NOTIFICATIONS("/api/v2/pp/notification/notifications"),
    SUBSCRIPTIONS("/api/v2/pp/notification/subscriptions");

    private final String path;
    
    RoutesV2(String path) {
        this.path = path;
    }
    
    public String getPath() {
        return path;
    }
}