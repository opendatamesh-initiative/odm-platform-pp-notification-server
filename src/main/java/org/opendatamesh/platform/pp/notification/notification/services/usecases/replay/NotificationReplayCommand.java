package org.opendatamesh.platform.pp.notification.notification.services.usecases.replay;

public class NotificationReplayCommand {
    private final Long notificationId;

    public NotificationReplayCommand(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Long getNotificationId() {
        return notificationId;
    }
}



