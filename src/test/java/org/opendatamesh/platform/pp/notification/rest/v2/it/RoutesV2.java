package org.opendatamesh.platform.pp.notification.rest.v2.it;

public enum RoutesV2 {
    
    SUBSCRIPTIONS("/api/v2/pp/notification/subscriptions"),
    EVENTS("/api/v2/pp/notification/events"),
    NOTIFICATIONS("/api/v2/pp/notification/notifications"),
    DISPATCH("/api/v2/pp/notification/dispatch/dispatch");
    
    private final String path;
    
    RoutesV2(String path) {
        this.path = path;
    }
    
    public String getPath() {
        return path;
    }
}

