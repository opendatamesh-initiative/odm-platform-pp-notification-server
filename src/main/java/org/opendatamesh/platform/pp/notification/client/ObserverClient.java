package org.opendatamesh.platform.pp.notification.client;

import org.opendatamesh.platform.pp.notification.notification.entities.Notification;

public interface ObserverClient {
    void dispatchNotification(Notification notification);
}
