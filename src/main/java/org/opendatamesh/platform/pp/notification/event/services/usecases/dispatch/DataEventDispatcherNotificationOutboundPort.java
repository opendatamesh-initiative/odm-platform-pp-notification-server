package org.opendatamesh.platform.pp.notification.event.services.usecases.dispatch;

import org.opendatamesh.platform.pp.notification.notification.entities.Notification;

public interface DataEventDispatcherNotificationOutboundPort {
    void sendNotification(Notification notification);
}


