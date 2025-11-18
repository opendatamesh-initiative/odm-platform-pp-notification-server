package org.opendatamesh.platform.pp.notification.rest;

public enum RoutesV1 {

    OBSERVERS("/api/v1/pp/notification/observers"),
    EVENTS("/api/v1/pp/notification/events"),
    NOTIFICATIONS("/api/v1/pp/notification/notifications"),
    DISPATCH("/api/v1/pp/notification/dispatch");

    private final String path;

    RoutesV1(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}