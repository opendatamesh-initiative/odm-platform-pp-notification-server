package org.opendatamesh.platform.pp.notification.event.services.usecases.dispatch;

import org.opendatamesh.platform.pp.notification.notification.entities.Notification;
import org.opendatamesh.platform.pp.notification.notification.services.NotificationService;
import org.springframework.stereotype.Component;

@Component
public class DataEventDispatcherNotificationOutboundPortImpl implements DataEventDispatcherNotificationOutboundPort {

    private final NotificationService notificationService;

    public DataEventDispatcherNotificationOutboundPortImpl(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void sendNotification(Notification notification) {
        notificationService.create(notification);
    }
}


