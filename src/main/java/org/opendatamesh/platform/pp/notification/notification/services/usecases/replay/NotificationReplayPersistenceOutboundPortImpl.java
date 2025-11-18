package org.opendatamesh.platform.pp.notification.notification.services.usecases.replay;

import org.opendatamesh.platform.pp.notification.notification.entities.Notification;
import org.opendatamesh.platform.pp.notification.notification.services.core.NotificationService;

class NotificationReplayPersistenceOutboundPortImpl implements NotificationReplayPersistenceOutboundPort {

    private final NotificationService notificationService;

    NotificationReplayPersistenceOutboundPortImpl(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public Notification findById(Long sequenceId) {
        return notificationService.findOne(sequenceId);
    }

    @Override
    public Notification update(Notification notification) {
        return notificationService.overwrite(notification.getSequenceId(), notification);
    }
}

