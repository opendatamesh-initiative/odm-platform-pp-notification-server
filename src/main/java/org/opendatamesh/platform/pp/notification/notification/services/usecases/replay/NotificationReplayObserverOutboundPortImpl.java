package org.opendatamesh.platform.pp.notification.notification.services.usecases.replay;

import org.opendatamesh.platform.pp.notification.client.ObserverClient;
import org.opendatamesh.platform.pp.notification.notification.entities.Notification;

class NotificationReplayObserverOutboundPortImpl implements NotificationReplayObserverOutboundPort {

    private final ObserverClient observerClient;

    NotificationReplayObserverOutboundPortImpl(ObserverClient observerClient) {
        this.observerClient = observerClient;
    }

    @Override
    public void dispatchNotification(Notification notification) {
        observerClient.dispatchNotification(notification);
    }
}

