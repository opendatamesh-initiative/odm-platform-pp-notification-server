package org.opendatamesh.platform.pp.notification.notification.entities;

// This enum must be modified with consciousness because incorrect states 
// can lead to race conditions between internal entity updates and observer updates.
public enum NotificationStatus {
    PROCESSING,
    PROCESSED,
    FAILED_TO_DELIVER,
    FAILED_TO_PROCESS
}
