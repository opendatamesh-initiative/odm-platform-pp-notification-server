package org.opendatamesh.platform.pp.notification.event.services.usecases.emit;

import org.opendatamesh.platform.pp.notification.notification.entities.Notification;
import org.opendatamesh.platform.pp.notification.notification.services.core.NotificationService;

class EventEmitterNotificationOutboundPortImpl implements EventEmitterNotificationOutboundPort {

    private final NotificationService notificationService;

    EventEmitterNotificationOutboundPortImpl(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public Notification create(Notification notification) {
        return notificationService.create(notification);
    }

    @Override
    public Notification update(Notification notification) {
        return notificationService.overwrite(notification.getSequenceId(), notification);
    }

    @Override
    public Notification findById(Long sequenceId) {
        return notificationService.findOne(sequenceId);
    }
}

