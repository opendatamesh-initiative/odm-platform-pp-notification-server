package org.opendatamesh.platform.pp.notification.notification.services.usecases.replay;

import org.opendatamesh.platform.pp.notification.notification.entities.Notification;

public interface NotificationReplayPresenter {
    void presentReplayedNotification(Notification notification);
}

