package org.opendatamesh.platform.pp.notification.notification.services.usecases.replay;

import org.opendatamesh.platform.pp.notification.notification.entities.Notification;

interface NotificationReplayObserverOutboundPort {
    void dispatchNotification(Notification notification);
}

