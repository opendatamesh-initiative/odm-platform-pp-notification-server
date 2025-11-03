package org.opendatamesh.platform.pp.notification.notification.services.usecases.replay;

import org.opendatamesh.platform.pp.notification.notification.services.NotificationService;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationRes;
import org.springframework.stereotype.Component;

@Component
public class NotificationReplayPersistenceOutboundPortImpl implements NotificationReplayPersistenceOutboundPort {

    private final NotificationService notificationService;

    public NotificationReplayPersistenceOutboundPortImpl(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public NotificationRes findNotificationById(Long notificationId) {
        try {
            return notificationService.findOneResource(notificationId);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public NotificationRes saveNotification(NotificationRes notification) {
        return notificationService.createResource(notification);
    }
}


