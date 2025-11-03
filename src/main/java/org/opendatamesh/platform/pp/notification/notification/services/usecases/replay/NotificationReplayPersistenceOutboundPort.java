package org.opendatamesh.platform.pp.notification.notification.services.usecases.replay;

import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationRes;

public interface NotificationReplayPersistenceOutboundPort {
    NotificationRes findNotificationById(Long notificationId);
    NotificationRes saveNotification(NotificationRes notification);
}

